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
public class Collected {

    private boolean vehicleFound; //localização da coleta

    private String note; // Observações

    private LocalDateTime collectionDateTime;

}
