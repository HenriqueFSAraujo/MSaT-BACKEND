package com.montreal.msiav_bh.controller;

import com.montreal.msiav_bh.dto.request.ZFaceRequest;
import com.montreal.oauth.domain.dto.response.FacialBiometricResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "ZFace")
public interface IZFaceApi {

    @PostMapping("/face/compare")
    @Operation(summary = "Comparar duas imagens",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Imagens comparadas com sucesso",
                            content = @Content(schema = @Schema(implementation = FacialBiometricResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Acesso não autorizado"
                    )
            }
    )
    FacialBiometricResponse compareImage(@RequestBody @Valid ZFaceRequest zFaceRequest);

}
