package com.montreal.msiav_bh.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.controller.base.BaseController;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.response.CompanyTypeResponse;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/company")
@Tag(name = "Empresa", description = "Operações de CRUD para gerenciamento de empresas")
public class CompanyController extends BaseController<CompanyCollection, CompanyDTO> {

    public CompanyController(CompanyService companyService) {
        super(companyService);
    }

    @Operation(summary = "Listar todos os tipos de empresas")
    @GetMapping("/types")
    public ResponseEntity<List<CompanyTypeResponse>> listCompanyTypes() {
        List<CompanyTypeResponse> companyTypes = Arrays.stream(CompanyTypeEnum.values())
                .map(type -> new CompanyTypeResponse(type.getCode(), type.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(companyTypes);
    }
}
