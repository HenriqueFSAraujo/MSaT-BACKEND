package com.montreal.msiav_bh.service;

import java.util.List;
import java.util.Map;
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
import com.montreal.msiav_bh.collection.HistoryCollection;
import com.montreal.msiav_bh.collection.VehicleCollection;
import com.montreal.msiav_bh.dto.AddressDTO;
import com.montreal.msiav_bh.dto.HistoryDTO;
import com.montreal.msiav_bh.dto.response.HistoryLocationInfo;
import com.montreal.msiav_bh.mapper.IAddressMapper;
import com.montreal.msiav_bh.mapper.IHistoryMapper;
import com.montreal.msiav_bh.repository.HistoryRepository;
import com.montreal.msiav_bh.repository.VehicleRepository;
import com.montreal.msiav_bh.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HistoryService extends BaseService<HistoryCollection, HistoryDTO> {

    private final VehicleRepository vehicleRepository;
    private final IAddressMapper addressMapper;
    private final VehicleService vehicleService;
    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryService(
            HistoryRepository historyRepository,
            IHistoryMapper historyMapper,
            MongoTemplate mongoTemplate,
            VehicleRepository vehicleRepository,
            IAddressMapper addressMapper,
            VehicleService vehicleService) {
        super(historyRepository, historyMapper, mongoTemplate, HistoryCollection.class);
        this.vehicleRepository = vehicleRepository;
        this.addressMapper = addressMapper;
        this.vehicleService = vehicleService;
        this.historyRepository = historyRepository;
    }

    @Override
    protected void setEntityId(HistoryCollection entity, String id) {
        entity.setId(id);
    }

    public Page<HistoryDTO> findAll(Pageable pageable, Map<String, String> searchCriteria) {
        Query query = new Query().with(pageable);

        // Construção dinâmica dos critérios de busca com base no Map usando AND
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            searchCriteria.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .forEach(entry -> query.addCriteria(Criteria.where(entry.getKey()).regex(entry.getValue(), "i")));
        }

        List<HistoryCollection> histories = mongoTemplate.find(query, HistoryCollection.class);
        Page<HistoryCollection> historyPage = PageableExecutionUtils.getPage(histories, pageable,
            () -> mongoTemplate.count(query, HistoryCollection.class));
        return historyPage.map(mapper::toDto);
    }

    @Override
    public HistoryDTO save(HistoryDTO dto) {
        validateFields(dto);
        verifyVehicleExists(dto.getIdVehicle());
        return super.save(dto);
    }

    @Override
    public HistoryDTO update(HistoryDTO dto, String id) {
        validateFields(dto);
        verifyVehicleExists(dto.getIdVehicle());
        return super.update(dto, id);
    }

    private void validateFields(HistoryDTO dto) {
        if (dto.getIdVehicle() == null || dto.getIdVehicle().trim().isEmpty()) {
            throw new IllegalArgumentException("idVehicle não pode ser nulo ou vazio");
        }
        if (dto.getLicensePlate() == null || dto.getLicensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("licensePlate não pode ser nulo ou vazio");
        }
    }

    private void verifyVehicleExists(String vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new NotFoundException("Veículo não encontrado com ID: " + vehicleId);
        }
    }

    public List<HistoryLocationInfo> listAllByLicensePlate(String licensePlate) {
        log.info("Listando todos os históricos para a placa do veículo {}", licensePlate);

        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            log.error("A placa do veículo é nula ou vazia");
            throw new IllegalArgumentException("A placa do veículo não pode ser nula ou vazia");
        }

        VehicleCollection vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado para a placa: " + licensePlate));

        List<HistoryCollection> histories = historyRepository.findByIdVehicle(vehicle.getId());

        return histories.stream()
                .filter(history -> history.getLocation() != null && history.getLocation().getAddress() != null)
                .map(history -> {
                    AddressCollection address = addressMapper.toEntity(history.getLocation().getAddress());
                    AddressDTO addressDTO = addressMapper.toDto(address);

                    return HistoryLocationInfo.builder()
                            .address(addressDTO)
                            .typeHistory(history.getTypeHistory())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Page<HistoryDTO> listAllByIdVehiclePage(String vehicleId, Pageable pageable) {
        log.info("Listando todos os históricos para o ID do veículo {}", vehicleId);

        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do veículo não pode ser nulo ou vazio");
        }

        verifyVehicleExists(vehicleId);

        Query query = new Query(Criteria.where("idVehicle").is(vehicleId)).with(pageable);
        List<HistoryCollection> histories = mongoTemplate.find(query, HistoryCollection.class);
        Page<HistoryCollection> historyPage = PageableExecutionUtils.getPage(histories, pageable, () -> mongoTemplate.count(query, HistoryCollection.class));
        
        if (historyPage.isEmpty()) {
            throw new NotFoundException("Nenhum histórico encontrado para o ID do veículo: " + vehicleId);
        }

        return historyPage.map(mapper::toDto);
    }
    
    public List<HistoryDTO> listAllByIdVehicle(String vehicleId) {
        log.info("Listando todos os históricos para o ID do veículo {}", vehicleId);

        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do veículo não pode ser nulo ou vazio");
        }

        verifyVehicleExists(vehicleId);

        List<HistoryCollection> histories = historyRepository.findByIdVehicle(vehicleId);

        if (histories.isEmpty()) {
            throw new NotFoundException("Nenhum histórico encontrado para o ID do veículo: " + vehicleId);
        }

        return histories.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
