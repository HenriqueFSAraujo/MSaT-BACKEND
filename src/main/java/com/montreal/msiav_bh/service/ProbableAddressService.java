package com.montreal.msiav_bh.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.collection.AddressCollection;
import com.montreal.msiav_bh.collection.VehicleAddressCollection;
import com.montreal.msiav_bh.dto.ProbableAddressDTO;
import com.montreal.msiav_bh.mapper.ProblableAdressMapper;
import com.montreal.msiav_bh.repository.AddressRepository;
import com.montreal.msiav_bh.repository.ProbableAddressRepository;
import com.montreal.msiav_bh.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProbableAddressService extends BaseService<VehicleAddressCollection, ProbableAddressDTO> {

    private final MongoTemplate mongoTemplate;
    private final AddressRepository addressRepository;
    private final ProbableAddressRepository probableAddressRepository;

    @Autowired
    public ProbableAddressService(
            MongoTemplate mongoTemplate,
            AddressRepository addressRepository,
            ProbableAddressRepository probableAddressRepository,
            ProblableAdressMapper problableAdressMapper) {
        super(probableAddressRepository, problableAdressMapper, mongoTemplate, VehicleAddressCollection.class);
        this.mongoTemplate = mongoTemplate;
        this.addressRepository = addressRepository;
        this.probableAddressRepository = probableAddressRepository;
    }

    public ProbableAddressDTO save(String vehicleId, AddressCollection address) {
        log.info("Verificando se o endereço já existe para VeículoID={}", vehicleId);

        List<AddressCollection> existingAddresses = addressRepository.findByPostalCodeAndStreetAndNumberAndNeighborhoodAndCity(
                address.getPostalCode(),
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCity()
        );

        AddressCollection savedAddress;
        if (!existingAddresses.isEmpty()) {
            log.info("Endereço já existe, usando o endereço existente para VeículoID={}", vehicleId);
            savedAddress = existingAddresses.get(0);
        } else {
            log.info("Criando um novo endereço para VeículoID={}", vehicleId);
            savedAddress = addressRepository.save(address);
        }

        Optional<VehicleAddressCollection> existingVehicleAddress =
                probableAddressRepository.findByVehicleIdAndAddressId(vehicleId, savedAddress.getId());

        if (existingVehicleAddress.isPresent()) {
            log.info("Associação já existe para VeículoID={}. Realizando atualização...", vehicleId);
            return update(vehicleId, savedAddress.getId(), address);
        }

        VehicleAddressCollection vehicleAddress = VehicleAddressCollection.builder()
                .vehicleId(vehicleId)
                .addressId(savedAddress.getId())
                .associatedDate(LocalDateTime.now())
                .build();

        probableAddressRepository.save(vehicleAddress);

        return ProbableAddressDTO.builder()
                .id(vehicleAddress.getId())
                .vehicleId(vehicleId)
                .address(savedAddress)
                .build();
    }

    public ProbableAddressDTO update(String vehicleId, String addressId, AddressCollection address) {
        log.info("Atualizando endereço provável para VeículoID={} e AddressID={}", vehicleId, addressId);

        probableAddressRepository.findByAddressIdAndVehicleId(addressId, vehicleId)
                .orElseThrow(() -> new NotFoundException("Associação de endereço e veículo não encontrada"));

        AddressCollection existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com ID: " + addressId));

        existingAddress.setPostalCode(address.getPostalCode());
        existingAddress.setStreet(address.getStreet());
        existingAddress.setNumber(address.getNumber());
        existingAddress.setNeighborhood(address.getNeighborhood());
        existingAddress.setComplement(address.getComplement());
        existingAddress.setState(address.getState());
        existingAddress.setCity(address.getCity());
        existingAddress.setNote(address.getNote());

        AddressCollection updatedAddress = addressRepository.save(existingAddress);
        log.info("Endereço atualizado com sucesso com ID={}", addressId);

        return ProbableAddressDTO.builder()
                .id(addressId)
                .vehicleId(vehicleId)
                .address(updatedAddress)
                .build();
    }

    public List<ProbableAddressDTO> findByVehicleId(String vehicleId) {
        log.info("Buscando endereços prováveis para VeículoID={}", vehicleId);

        List<VehicleAddressCollection> vehicleAddresses = probableAddressRepository.findByVehicleId(vehicleId);

        if (vehicleAddresses.isEmpty()) {
            throw new NotFoundException("Não foram encontrados endereços prováveis para o veículo especificado");
        }

        return vehicleAddresses.stream()
                .map(vehicleAddress -> {
                    AddressCollection address = addressRepository.findById(vehicleAddress.getAddressId())
                            .orElseThrow(() -> new NotFoundException("Endereço não encontrado com ID: " + vehicleAddress.getAddressId()));
                    return ProbableAddressDTO.builder()
                            .id(vehicleAddress.getId())
                            .vehicleId(vehicleId)
                            .address(address)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ProbableAddressDTO findByAddressId(String addressId) {
        log.info("Buscando endereço provável com AddressID={}", addressId);

        AddressCollection address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado com ID: " + addressId));

        VehicleAddressCollection vehicleAddress = probableAddressRepository.findByAddressId(addressId)
                .orElseThrow(() -> new NotFoundException("Associação de endereço não encontrada para o AddressID: " + addressId));

        return ProbableAddressDTO.builder()
                .id(vehicleAddress.getId())
                .vehicleId(vehicleAddress.getVehicleId())
                .address(address)
                .build();
    }

    public void delete(String addressId, String vehicleId) {
        VehicleAddressCollection vehicleAddress = probableAddressRepository.findByAddressIdAndVehicleId(addressId, vehicleId)
                .orElseThrow(() -> new NotFoundException("Associação de endereço e veículo não encontrada"));

        log.info("Removendo endereço provável com ID={} para VeículoID={}", addressId, vehicleId);

        probableAddressRepository.delete(vehicleAddress);
        addressRepository.deleteById(addressId);
    }

    @Override
    public Page<ProbableAddressDTO> findAll(Pageable pageable, Map<String, String> searchCriteria) {
        Query query = new Query().with(pageable);

        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            searchCriteria.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .forEach(entry -> query.addCriteria(Criteria.where(entry.getKey()).regex(entry.getValue(), "i")));
        }

        List<VehicleAddressCollection> vehicleAddresses = mongoTemplate.find(query, VehicleAddressCollection.class);
        Page<VehicleAddressCollection> vehicleAddressPage = PageableExecutionUtils.getPage(vehicleAddresses, pageable,
            () -> mongoTemplate.count(query, VehicleAddressCollection.class));
        return vehicleAddressPage.map(mapper::toDto);
    }

    @Override
    protected void setEntityId(VehicleAddressCollection entity, String id) {
        entity.setId(id);
    }
}
