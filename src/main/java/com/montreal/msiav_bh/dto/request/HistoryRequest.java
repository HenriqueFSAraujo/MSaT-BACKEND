package com.montreal.msiav_bh.dto.request;

import com.montreal.msiav_bh.dto.CollectedDTO;
import com.montreal.msiav_bh.dto.ImpoundLotDTO;
import com.montreal.msiav_bh.dto.LocationDTO;
import com.montreal.msiav_bh.enumerations.TypeHistoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequest {

    @NotBlank
    @Schema(description = "Identificador do veículo")
    private String idVehicle;

    @NotNull
    @Schema(description = "Tipo de histórico (LOCATION, COLLECTED, IMPOUND_LOT)")
    private TypeHistoryEnum typeHistory;

    @Schema(description = "Informações de localização do veículo")
    private LocationDTO location;

    @Schema(description = "Informações de apreensão do veículo")
    private CollectedDTO collected;

    @Schema(description = "Informações do pátio de apreensão")
    private ImpoundLotDTO impoundLot;

}
