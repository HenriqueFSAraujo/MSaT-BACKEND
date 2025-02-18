package com.montreal.msiav_bh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.request.VehicleFilterRequest;
import com.montreal.msiav_bh.dto.response.HistoryLocationInfo;
import com.montreal.msiav_bh.dto.response.PageVehicleResponse;
import com.montreal.msiav_bh.dto.response.VehicleResponse;
import com.montreal.msiav_bh.enumerations.VehicleStageEnum;
import com.montreal.msiav_bh.enumerations.VehicleStatusEnum;
import com.montreal.msiav_bh.mapper.ICompanyMapper;
import com.montreal.msiav_bh.service.HistoryService;
import com.montreal.msiav_bh.service.VehicleCompanyService;
import com.montreal.msiav_bh.service.VehicleService;
import com.montreal.msiav_rio.model.response.ContractCompleteResponse;
import com.montreal.msiav_rio.model.response.VeiculoDTO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Veículo", description = "Endpoints para gerenciamento de veículos")
@ApiResponses({
    @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
})
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleCompanyService vehicleCompanyService;
    private final HistoryService historyService;
    private final ICompanyMapper companyMapper;


    @GetMapping("/vehicle")
    @ApiResponse(responseCode = "200", description = "Listar todos os veículos por período")
    public PageVehicleResponse listAll(@Parameter(hidden = true) @Valid VehicleFilterRequest filter, Pageable page) {
        log.info("Iniciando busca de veículos com filtro: {}", filter);
        try {
            PageVehicleResponse response = vehicleService.listAllAndApiRio(page, filter);
            log.info("Busca de veículos concluída com sucesso.");
            return response;
        } catch (Exception e) {
            log.error("Erro ao listar veículos por período", e);
            throw new RuntimeException("Erro ao listar veículos. Por favor, tente novamente mais tarde.", e);
        }
    }

    @GetMapping("/vehicle/{numberContract}")
    @ApiResponse(responseCode = "200", description = "Lista dados do contrato por número do contrato")
    public ContractCompleteResponse findContractByNumberContract(@PathVariable String numberContract) {
        log.info("Iniciando busca de contrato com número: {}", numberContract);

        List<CompanyDTO> empresasDTO = new ArrayList<>();
        List<HistoryLocationInfo> historicos = new ArrayList<>();

        try {
            ContractCompleteResponse contractResponse = vehicleService.findContractByNumberContract(numberContract);

            if (contractResponse != null && contractResponse.getData() != null) {
                List<VeiculoDTO> veiculos = contractResponse.getData().getVeiculos();

                if (!CollectionUtils.isEmpty(veiculos)) {
                    for (VeiculoDTO veiculo : veiculos) {
                        List<CompanyCollection> companies = vehicleCompanyService.findCompaniesByVehicleLicensePlate(veiculo.getPlaca());
                        
                        List<CompanyDTO> companiesDTO = companies.stream()
                                .map(companyMapper::toDto)
                                .toList();

                        empresasDTO.addAll(companiesDTO);

                        List<HistoryLocationInfo> vehicleHistories = historyService.listAllByLicensePlate(veiculo.getPlaca());
                        historicos.addAll(vehicleHistories);
                    }

                    contractResponse.getData().setEmpresas(empresasDTO);
                    contractResponse.getData().setHistoricos(historicos);
                }
            }
            log.info("Busca de contrato com número {} concluída com sucesso.", numberContract);
            return contractResponse;
        } catch (Exception e) {
            log.error("Erro ao buscar contrato com número {}: {}", numberContract, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar dados do contrato. Por favor, tente novamente mais tarde.", e);
        }
    }
    
    @PatchMapping("/vehicle/status/{vehicleId}")
    @ApiResponse(responseCode = "200", description = "Alterar status do veículo por ID")
    public VehicleResponse updateStatusByVehicleId(VehicleStatusEnum status, VehicleStageEnum stage, @PathVariable String vehicleId) {
        try {
            VehicleResponse response = vehicleService.alterStageAndStatus(stage, status, vehicleId);
            log.info("Status do veículo com ID {} atualizado com sucesso.", vehicleId);
            return response;
        } catch (Exception e) {
            log.error("Erro ao atualizar status do veículo com ID {}: {}", vehicleId, e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar o status do veículo. Por favor, tente novamente mais tarde.", e);
        }
    }

    @PatchMapping("/vehicle/date-seizure/{vehicleId}")
    @ApiResponse(responseCode = "200", description = "Incluir data de apreensão do veículo")
    public VehicleResponse updateDateSeizureByVehicleId(@PathVariable String vehicleId) {
        log.info("Iniciando inclusão da data de apreensão para o veículo com ID: {}", vehicleId);
        try {
            VehicleResponse response = vehicleService.updateDateSeizureByVehicleId(vehicleId);
            log.info("Data de apreensão incluída com sucesso para o veículo com ID: {}", vehicleId);
            return response;
        } catch (Exception e) {
            log.error("Erro ao incluir data de apreensão para o veículo com ID {}: {}", vehicleId, e.getMessage(), e);
            throw new RuntimeException("Erro ao incluir a data de apreensão do veículo. Por favor, tente novamente mais tarde.", e);
        }
    }

    @GetMapping("/vehicle/id/{vehicleId}")
    public VehicleResponse findVehicleById(@PathVariable String vehicleId) {
        log.info("Iniciando busca do veículo com ID: {}", vehicleId);

        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            log.error("ID do veículo é nulo ou vazio");
            throw new IllegalArgumentException("ID do veículo não pode ser nulo ou vazio");
        }

        try {
            VehicleResponse vehicleResponse = vehicleService.findVehicleById(vehicleId);
            log.info("Veículo com ID {} encontrado com sucesso", vehicleId);
            return vehicleResponse;
        } catch (Exception e) {
            log.error("Erro ao buscar veículo com ID {}: {}", vehicleId, e.getMessage());
            throw new RuntimeException("Erro ao buscar veículo. Por favor, tente novamente.", e);
        }
    }
}