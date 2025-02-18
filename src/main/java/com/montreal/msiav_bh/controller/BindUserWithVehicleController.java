package com.montreal.msiav_bh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.service.BindUserWithVehicleService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class BindUserWithVehicleController {

	private final BindUserWithVehicleService bindUserWithVehicleService;
	
	@PostMapping("/bind/agente-oficial/company")
	public ResponseEntity<String> bindUserAgenteOficialWithVehicleByCompany(
	        @RequestParam String companyId,
	        @RequestParam String vehicleId
	) {
	    bindUserWithVehicleService.bindUserAgenteOficialWithVehicleByCompany(companyId, vehicleId);
	    return ResponseEntity.ok("Vínculo criado para usuários da empresa com o veículo.");
	}


    @PostMapping("/bind/agente-oficial")
    public ResponseEntity<String> bindUserWithVehicle(
            @RequestParam Long userId,
            @RequestParam String vehicleId,
            @RequestParam Long associatedBy
    ) {
        String message = bindUserWithVehicleService.bindUserWithVehicleAgenteOficial(userId, vehicleId, associatedBy);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/unbind/agente-oficial")
    public ResponseEntity<String> unbindUserWithVehicle(
            @RequestParam Long userId,
            @RequestParam String vehicleId
    ) {
        String message = bindUserWithVehicleService.unbindUserWithVehicleAgenteOficial(userId, vehicleId);
        return ResponseEntity.ok(message);
    }

    
}