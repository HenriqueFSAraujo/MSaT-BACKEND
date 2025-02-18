package com.montreal.msiav_bh.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.collection.HistoryCollection;
import com.montreal.msiav_bh.controller.base.BaseController;
import com.montreal.msiav_bh.dto.HistoryDTO;
import com.montreal.msiav_bh.service.HistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/history")
@Tag(name = "Historico", description = "Operações de CRUD para gerenciamento de históricos")
public class HistoryController extends BaseController<HistoryCollection, HistoryDTO> {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        super(historyService);
        this.historyService = historyService;
    }

    @Operation(summary = "Listar histórico por ID do veículo")
    @GetMapping("/by-vehicle")
    public Page<HistoryDTO> listAllByIdVehicle(@RequestParam String vehicleId, Pageable pageable) {
        return historyService.listAllByIdVehiclePage(vehicleId, pageable);
    }

}
