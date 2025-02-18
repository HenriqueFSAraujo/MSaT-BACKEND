package com.montreal.integration.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.integration.response.IntegrationDataResponse;
import com.montreal.msiav_bh.dto.AddressDTO;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.HistoryDTO;
import com.montreal.msiav_bh.dto.response.VehicleResponse;
import com.montreal.msiav_bh.mapper.IVehicleMapper;
import com.montreal.msiav_bh.service.AddressService;
import com.montreal.msiav_bh.service.CompanyService;
import com.montreal.msiav_bh.service.HistoryService;
import com.montreal.msiav_bh.service.VehicleAddressService;
import com.montreal.msiav_bh.service.VehicleCompanyService;
import com.montreal.msiav_bh.service.VehicleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final VehicleService vehicleService;
    private final AddressService addressService;
    private final CompanyService companyService;
    private final HistoryService historyService;
    private final VehicleAddressService vehicleAddressService;
    private final VehicleCompanyService vehicleCompanyService;

    public IntegrationDataResponse getIntegrationData(String numberContract) {

        log.info("Buscando dados de integração para o contrato {}", numberContract);

        var vehicle = getVehicle(numberContract);
        var addresses = getAddresses(vehicle.getId());
        var companies = getCompanies(vehicle.getId());
        var histories = getHistories(vehicle.getId());

        var integrationData =  IntegrationDataResponse.builder()
                .vehicleData(vehicle)
                .addresses(addresses)
                .companies(companies)
                .histories(histories)
                .build();

        log.info("Dados de integração para o contrato {} encontrados", numberContract);

        return integrationData;

    }

    private VehicleResponse getVehicle(String numberContract) {
        var vehicle =  vehicleService.findByContractNumber(numberContract);
        return IVehicleMapper.INSTANCE.toVehicleResponse(vehicle);
    }

    private List<AddressDTO> getAddresses(String vehicleId) {
        List<AddressDTO> addresses = new ArrayList<>();
        try {

            var addressesVehicle = vehicleAddressService.findByVehicleId(vehicleId);

            addressesVehicle.forEach(vehicleAddress -> {
                try {
                    addresses.add(addressService.findById(vehicleAddress.getAddressId()));
                } catch (NotFoundException e) {
                    log.error("Endereço {} não encontrado", vehicleAddress.getAddressId());
                }
            });

        } catch (NotFoundException e) {
            log.error("Endereços para o veiculo {} não encontrado", vehicleId);
        }

        return addresses;

    }

    private List<CompanyDTO> getCompanies(String vehicleId) {
        List<CompanyDTO> companies = new ArrayList<>();
        try {

            var companiesVehicle = vehicleCompanyService.findByVehicleId(vehicleId);

            companiesVehicle.forEach(vehicleCompany -> {
                try {
                    companies.add(companyService.findById(vehicleCompany.getCompanyId()));
                } catch (NotFoundException e) {
                    log.error("Empresa {} não encontrada", vehicleCompany.getCompanyId());
                }
            });

        } catch (NotFoundException e) {
            log.error("Empresas para o veiculo {} não encontrada", vehicleId);
        }

        return companies;

    }

    private List<HistoryDTO> getHistories(String vehicleId) {
        List<HistoryDTO> histories = new ArrayList<>();
        try {

            histories = historyService.listAllByIdVehicle(vehicleId);

        } catch (NotFoundException e) {
            log.error("Históricos para o veiculo {} não encontrada", vehicleId);
        }

        return histories;

    }

}
