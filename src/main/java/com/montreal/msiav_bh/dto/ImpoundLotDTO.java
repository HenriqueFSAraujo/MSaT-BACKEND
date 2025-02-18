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
public class ImpoundLotDTO {

    @JsonProperty("impound_arrival_date_time")
    @Schema(description = "Data e hora de chegada no pátio")
    private LocalDateTime impoundArrivalDateTime;

    @JsonProperty("impound_departure_date_time")
    @Schema(description = "Data e hora de saída do pátio")
    private LocalDateTime impoundDepartureDateTime;
}
