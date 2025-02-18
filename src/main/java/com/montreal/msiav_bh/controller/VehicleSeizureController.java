package com.montreal.msiav_bh.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.dto.VehicleSeizureDTO;
import com.montreal.msiav_bh.service.VehicleSeizureService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/vehicle-seizures")
@RequiredArgsConstructor
@Tag(name = "Vehicle Seizure", description = "Endpoints para gerenciamento de apreensões de veículos")
@ApiResponses({
    @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
    @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
})
public class VehicleSeizureController {

    private final VehicleSeizureService vehicleSeizureService;

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Listar todas as apreensões de veículos")
    public Page<VehicleSeizureDTO> listAll(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        log.info("Iniciando busca de todas as apreensões de veículos");
        try {
            return vehicleSeizureService.findAll(pageable);
        } catch (Exception e) {
            log.error("Erro ao listar apreensões de veículos: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Buscar apreensão de veículo por ID")
    public VehicleSeizureDTO findById(@PathVariable String id) {
        log.info("Iniciando busca de apreensão de veículo com ID: {}", id);
        try {
            return vehicleSeizureService.findById(id);
        } catch (Exception e) {
            log.error("Erro ao buscar apreensão de veículo com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Criar nova apreensão de veículo")
    public VehicleSeizureDTO create(@RequestBody @Valid VehicleSeizureDTO dto) {
        log.info("Iniciando criação de nova apreensão de veículo");
        try {
            return vehicleSeizureService.create(dto);
        } catch (Exception e) {
            log.error("Erro ao criar apreensão de veículo: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Atualizar apreensão de veículo por ID")
    public VehicleSeizureDTO update(@PathVariable String id, @RequestBody @Valid VehicleSeizureDTO dto) {
        log.info("Iniciando atualização de apreensão de veículo com ID: {}", id);
        try {
            return vehicleSeizureService.update(id, dto);
        } catch (Exception e) {
            log.error("Erro ao atualizar apreensão de veículo com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Excluir apreensão de veículo por ID")
    public void delete(@PathVariable String id) {
        log.info("Iniciando exclusão de apreensão de veículo com ID: {}", id);
        try {
            vehicleSeizureService.delete(id);
            log.info("Apreensão de veículo com ID {} excluída com sucesso.", id);
        } catch (Exception e) {
            log.error("Erro ao excluir apreensão de veículo com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
