package com.montreal.msiav_bh.controller;

import java.util.List;

import com.montreal.core.domain.exception.NegocioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.montreal.msiav_bh.dto.VehicleImagesDTO;
import com.montreal.msiav_bh.service.VehicleImagesService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/vehicle-seizures-images")
@RequiredArgsConstructor
@Tag(name = "Vehicle Images", description = "Endpoints para gerenciamento de imagens de veículos")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
        @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
})
public class VehicleImagesController {

    private final VehicleImagesService vehicleImagesService;

    @GetMapping("/{seizureId}")
    @ApiResponse(responseCode = "200", description = "Listar todas as imagens de uma apreensão de veículo")
    public Page<VehicleImagesDTO> listAllBySeizureId(
            @PathVariable String seizureId,
            @PageableDefault Pageable pageable) {
        log.info("Buscando todas as imagens para a apreensão com ID: {}", seizureId);
        return vehicleImagesService.findAllBySeizureId(seizureId, pageable);
    }

    @PostMapping(value = "/{seizureId}", consumes = {"multipart/form-data"})
    @ApiResponse(responseCode = "201", description = "Adicionar novas imagens para uma apreensão de veículo")
    public List<VehicleImagesDTO> addImages(
            @PathVariable String seizureId,
            @RequestParam List<String> visionTypes,
            @RequestPart("images") List<MultipartFile> imageFiles) {
        log.info("Adicionando {} imagens para a apreensão com ID: {}", imageFiles.size(), seizureId);

        // Valida se o número de tipos de visão corresponde ao número de imagens
        if (visionTypes.size() != imageFiles.size()) {
            throw new NegocioException("O número de tipos de visão não corresponde ao número de imagens enviadas.");
        }

        return vehicleImagesService.saveImages(seizureId, imageFiles, visionTypes);
    }

    @DeleteMapping("/{imageId}")
    @ApiResponse(responseCode = "204", description = "Excluir uma imagem por ID")
    public void deleteImage(@PathVariable String imageId) {
        log.info("Excluindo imagem com ID: {}", imageId);
        vehicleImagesService.deleteImage(imageId);
    }
}
