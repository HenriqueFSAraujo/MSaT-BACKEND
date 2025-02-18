package com.montreal.msiav_bh.dto;

import java.time.LocalDateTime;

import com.montreal.msiav_bh.dto.response.VehicleResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCompanyDTO {

    @Schema(description = "Identificador único do registro")
    private String id;

    @Schema(description = "Identificador único da empresa associada")
    private String companyId;

    @Schema(description = "Identificador único do veículo associado")
    private String vehicleId;

    @Schema(description = "Dados do veículo associado")
    private VehicleResponse vehicle;

    @Schema(description = "Dados da empresa associada")
    private CompanyDTO company;

    @Schema(description = "Data e hora em que o registro foi criado")
    private LocalDateTime createdAt;

    @Schema(description = "Tipo empresa")
    private String companyType;
}
