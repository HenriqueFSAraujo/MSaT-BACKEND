package com.montreal.msiav_bh.collection.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImpoundLot {

    @NotNull(message = "A data e hora de chegada ao pátio são obrigatórias.")
    private LocalDateTime impoundArrivalDateTime; // Data e hora de chegada ao pátio

    @NotNull(message = "A data e hora de saída do pátio são obrigatórias.")
    private LocalDateTime impoundDepartureDateTime; // Data e hora de saída do pátio

}
