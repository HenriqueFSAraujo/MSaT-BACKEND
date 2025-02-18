package com.montreal.msiav_bh.controller;

import java.util.List;
import java.util.Objects;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.dto.VehicleCompanyDTO;
import com.montreal.msiav_bh.dto.request.TriggerMultiVehicleplesRequest;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.service.VehicleCompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Veiculo Empresa")
public class VehicleCompanyController {

    private final VehicleCompanyService vehicleCompanyService;

    @PostMapping("/vehicle-company")
    @ResponseStatus(HttpStatus.CREATED)
    public void linked(@RequestBody @Valid VehicleCompanyDTO request) throws BadRequestException {
       
    	log.info("Iniciando vinculação. VeículoID={}, EmpresaID={}, TipoEmpresa={}", request.getVehicleId(), request.getCompanyId(), request.getCompanyType());
        try {
        	
            validarRequest(request);
            CompanyTypeEnum companyTypeEnum = CompanyTypeEnum.fromCode(request.getCompanyType());
            vehicleCompanyService.saveVehicleCompany(request.getVehicleId(), request.getCompanyId(), companyTypeEnum);
            log.info("Vinculação concluída com sucesso para VeículoID={} e EmpresaID={}.", request.getVehicleId(), request.getCompanyId());

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao processar a vinculação: {}", e.getMessage());
            throw new BadRequestException("Erro de validação: " + e.getMessage());
        } catch (NotFoundException e) {
            log.error("Erro ao processar vinculação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado durante o processo de vinculação", e);
            throw new RuntimeException("Erro interno ao processar a vinculação. Tente novamente mais tarde.", e);
        }
    }

    private void validarRequest(VehicleCompanyDTO request) {
        if (request.getVehicleId() == null) {
            throw new IllegalArgumentException("O campo 'vehicleId' é obrigatório.");
        }
        if (request.getCompanyId() == null) {
            throw new IllegalArgumentException("O campo 'companyId' é obrigatório.");
        }
        if (request.getCompanyType() == null || request.getCompanyType().isBlank()) {
            throw new IllegalArgumentException("O campo 'companyType' não pode ser nulo ou vazio.");
        }
    }

    @DeleteMapping("/vehicle-company/{companyId}/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removed(@PathVariable String companyId, @PathVariable String vehicleId) {
        vehicleCompanyService.deleteVehicleCompany(vehicleId, companyId);
    }

    @GetMapping("/vehicle-company/{vehicleId}")
    public List<VehicleCompanyDTO> findByVehicleId(@PathVariable String vehicleId) {
        return vehicleCompanyService.findByVehicleId(vehicleId);
    }

    @PatchMapping("/vehicle-company/trigger/{vehicleId}/{companyId}")
    public void trigger(@PathVariable String companyId, @PathVariable String vehicleId) {
        vehicleCompanyService.trigger(companyId, vehicleId);
    }

    @PostMapping("/vehicle-company/trigger-multiple/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public void triggerMultiple(@PathVariable String companyId,  @RequestBody TriggerMultiVehicleplesRequest request) {
        log.info("Recebendo requisição para acionar múltiplos veículos a empresa: EmpresaID={}, Veículos={}", companyId, request.getVehicleIds());
        try {
        	
            if (Objects.isNull(companyId)) {
                log.error("Requisição está nula para EmpresaID={}", companyId);
                throw new IllegalArgumentException("A requisição não pode ser nula.");
            }

            if (Objects.isNull(request.getVehicleIds()) || request.getVehicleIds().isEmpty()) {
                log.error("Conjunto de vehicleIds está nulo ou vazio para EmpresaID={}", companyId);
                throw new IllegalArgumentException("O conjunto de vehicleIds não pode estar nulo ou vazio.");
            }

            vehicleCompanyService.triggerMultiple(companyId, request.getVehicleIds());
            log.info("Eventos acionados com sucesso para EmpresaID={} e Veículos={}", companyId, request.getVehicleIds());
        } catch (NotFoundException e) {
            log.error("Erro ao acionar múltiplos eventos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao acionar múltiplos eventos: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao acionar múltiplos eventos", e);
        }
    }

    @PostMapping("/impound-lot-company/trigger-multiple/{impoundLotId}")
    @ResponseStatus(HttpStatus.OK)
    public void triggerMultiplePatioVehicle(@PathVariable String impoundLotId, @RequestBody TriggerMultiVehicleplesRequest request) {
        log.info("Recebendo requisição para acionar múltiplos veículos para o pátio: PatioID={}, Veículos={}", impoundLotId, request.getVehicleIds());
        try {
            if (Objects.isNull(impoundLotId)) {
                log.error("Requisição está nula para PatioID={}", impoundLotId);
                throw new IllegalArgumentException("A requisição não pode ser nula.");
            }

            if (Objects.isNull(request.getVehicleIds()) || request.getVehicleIds().isEmpty()) {
                log.error("Conjunto de vehicleIds está nulo ou vazio para PatioID={}", impoundLotId);
                throw new IllegalArgumentException("O conjunto de vehicleIds não pode estar nulo ou vazio.");
            }

            vehicleCompanyService.triggerMultiplePatioVehicle(impoundLotId, request.getVehicleIds());
            log.info("Eventos acionados com sucesso para PatioID={} e Veículos={}", impoundLotId, request.getVehicleIds());
        } catch (NotFoundException e) {
            log.error("Erro ao acionar múltiplos eventos para o pátio: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao acionar múltiplos eventos para o pátio: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao acionar múltiplos eventos para o pátio", e);
        }
    }

    @PostMapping("/tow-truck-company/trigger-multiple/{towTruckId}")
    @ResponseStatus(HttpStatus.OK)
    public void triggerMultipleTowTruckVehicle(@PathVariable String towTruckId, @RequestBody TriggerMultiVehicleplesRequest request) {
        log.info("Recebendo requisição para acionar múltiplos veículos para o guincho: TowTruckID={}, Veículos={}", towTruckId, request.getVehicleIds());
        try {
            if (Objects.isNull(towTruckId)) {
                log.error("Requisição está nula para TowTruckID={}", towTruckId);
                throw new IllegalArgumentException("A requisição não pode ser nula.");
            }

            if (Objects.isNull(request.getVehicleIds()) || request.getVehicleIds().isEmpty()) {
                log.error("Conjunto de vehicleIds está nulo ou vazio para TowTruckID={}", towTruckId);
                throw new IllegalArgumentException("O conjunto de vehicleIds não pode estar nulo ou vazio.");
            }

            vehicleCompanyService.triggerMultipleTowTruckVehicle(towTruckId, request.getVehicleIds());
            log.info("Eventos acionados com sucesso para TowTruckID={} e Veículos={}", towTruckId, request.getVehicleIds());
        } catch (NotFoundException e) {
            log.error("Erro ao acionar múltiplos eventos para o guincho: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao acionar múltiplos eventos para o guincho: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao acionar múltiplos eventos para o guincho", e);
        }
    }

    @PostMapping("/locator-company/trigger-multiple/{locatorId}")
    @ResponseStatus(HttpStatus.OK)
    public void triggerMultipleLocatorVehicle(@PathVariable String locatorId, @RequestBody TriggerMultiVehicleplesRequest request) {
        log.info("Recebendo requisição para acionar múltiplos veículos para o localizador: LocatorID={}, Veículos={}", locatorId, request.getVehicleIds());
        try {
            if (Objects.isNull(locatorId)) {
                log.error("Requisição está nula para LocatorID={}", locatorId);
                throw new IllegalArgumentException("A requisição não pode ser nula.");
            }

            if (Objects.isNull(request.getVehicleIds()) || request.getVehicleIds().isEmpty()) {
                log.error("Conjunto de vehicleIds está nulo ou vazio para LocatorID={}", locatorId);
                throw new IllegalArgumentException("O conjunto de vehicleIds não pode estar nulo ou vazio.");
            }

            vehicleCompanyService.triggerMultipleLocatorVehicle(locatorId, request.getVehicleIds());
            log.info("Eventos acionados com sucesso para LocatorID={} e Veículos={}", locatorId, request.getVehicleIds());
        } catch (NotFoundException e) {
            log.error("Erro ao acionar múltiplos eventos para o localizador: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao acionar múltiplos eventos para o localizador: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao acionar múltiplos eventos para o localizador", e);
        }
    }

}
