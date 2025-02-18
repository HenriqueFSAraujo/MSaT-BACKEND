package com.montreal.msiav_bh.service;

import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.collection.VehicleAddressCollection;
import com.montreal.msiav_bh.dto.response.VehicleAddressResponse;
import com.montreal.msiav_bh.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleAddressService {

    private final VehicleRepository vehicleRepository;
    private final AddressRepository addressRepository;
    private final VehicleAddressRepository vehicleAddressRepository;

    public void saveVehicleAddress(String vehicleId, String addressId) {
        log.info("Salvando relacionamento entre veiculo e empresa");

        verifyVehicleAndAddressExist(vehicleId, addressId);

        vehicleAddressRepository.save(VehicleAddressCollection.builder().vehicleId(vehicleId).addressId(addressId).build());
    }

    public void deleteVehicleAddress(String vehicleId, String addressId) {
        log.info("Deletando relacionamento entre veiculo e empresa");

        var vehicleCompany = vehicleAddressRepository.findByVehicleIdAndAddressId(vehicleId, addressId)
                .orElseThrow(() -> new NotFoundException("Relacionamento não encontrado"));

        vehicleAddressRepository.delete(vehicleCompany);
    }

    public List<VehicleAddressResponse> findByVehicleId(String vehicleId) {
        log.info("Buscando endereço do veiculo {}", vehicleId);

        var vehicleAddresses = vehicleAddressRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new NotFoundException("Nao foi encontrado endereço para o veiculo"));

        return vehicleAddresses.stream().map(vehicleAddress -> VehicleAddressResponse.builder()
                .id(vehicleAddress.getId())
                .vehicleId(vehicleAddress.getVehicleId())
                .addressId(vehicleAddress.getAddressId())
                .build()).toList();

    }

    private void verifyVehicleAndAddressExist(String vehicleId, String addressId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Veiculo não encontrado"));

        addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado"));
    }

}
