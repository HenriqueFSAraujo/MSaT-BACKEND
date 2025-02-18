package com.montreal.msiav_bh.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.collection.SeizureDateCollection;
import com.montreal.msiav_bh.dto.request.SeizureDateRequest;
import com.montreal.msiav_bh.dto.response.SeizureDateResponse;
import com.montreal.msiav_bh.mapper.SeizureDateMapper;
import com.montreal.msiav_bh.service.SeizureDateService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/seizure-dates")
@RequiredArgsConstructor
@Tag(name = "Apreensão de veículo", description = "Operações com data deapreensão de veículos")
public class SeizureDateController {

    private final SeizureDateService seizureDateService;
    private final SeizureDateMapper seizureDateMapper;

    @GetMapping("/{veiculeId}")
    public ResponseEntity<List<SeizureDateCollection>> getSeizureStatusByVehicleId(@PathVariable String veiculeId) {
        List<SeizureDateCollection> seizureDates = seizureDateService.findByVehicleId(veiculeId);
        return ResponseEntity.ok(seizureDates);
    }

    @PutMapping
    public ResponseEntity<SeizureDateCollection> updateSeizureStatus(@RequestBody SeizureDateCollection seizureDate) {
        SeizureDateCollection updatedSeizureDate = seizureDateService.update(seizureDate);
        return ResponseEntity.ok(updatedSeizureDate);
    }

    @PostMapping
    public ResponseEntity<SeizureDateResponse> addSeizureDateToVehicle(@RequestBody SeizureDateRequest seizureDateRequest) {
        
        // Chama o serviço para adicionar ou atualizar a apreensão do veículo
        SeizureDateCollection createdOrUpdatedSeizureDate = seizureDateService.addOrUpdateSeizureDateToVehicle(seizureDateRequest);

        SeizureDateResponse response = seizureDateMapper.toResponse(createdOrUpdatedSeizureDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

