package com.montreal.msiav_bh.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeizureDateResponse {

    private String vehicleId;
    private String companyId;

    @Schema(description = "Data e hora de apreensão")
    private LocalDateTime seizureDate;

}
