package com.montreal.msiav_bh.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZFaceRequest {

    @NotBlank
    @Schema(description = "Base64 da imagem 1")
    private String base64Image1;

    @NotBlank
    @Schema(description = "Base64 da imagem 2")
    private String base64Image2;

}
