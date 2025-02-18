package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectedDTO {

    @JsonProperty("vehicle_found")
    @Schema(description = "Veículo encontrado (true/false)")
    private boolean vehicleFound;

    @JsonProperty("note")
    @Schema(description = "Observações sobre a apreensão")
    private String note;

    @JsonProperty("collection_date_time")
    @Schema(description = "Data e hora da apreensão")
    private LocalDateTime collectionDateTime;
}
