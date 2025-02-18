package com.montreal.msiav_bh.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAddressResponse {

    @Schema(description = "Identificador único")
    private String id;

    @Schema(description = "Identificador do endereço associado")
    private String addressId;

    @Schema(description = "Identificador do veículo associado")
    private String vehicleId;

}
