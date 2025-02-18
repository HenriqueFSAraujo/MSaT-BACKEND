package com.montreal.msiav_bh.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFilterRequest {

    @Schema(description = "Data inicial da pequisa")
    private LocalDate startDate;

    @Schema(description = "Data final da pequisa")
    private LocalDate endDate;

}
