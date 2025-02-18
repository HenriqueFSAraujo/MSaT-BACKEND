package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    @JsonProperty("vehicle_found")
    @Schema(description = "Veículo encontrado (true/false)")
    private boolean vehicleFound;

    @JsonProperty("note")
    @Schema(description = "Observações sobre a localização")
    private String note;

    @JsonProperty("address")
    @Schema(description = "Endereço da localização")
    private AddressDTO address;
}
