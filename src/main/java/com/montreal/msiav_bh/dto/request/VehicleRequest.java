package com.montreal.msiav_bh.dto.request;

import com.montreal.msiav_bh.enumerations.VehicleStageEnum;
import com.montreal.msiav_bh.enumerations.VehicleStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    @Schema(description = "Placa do veículo")
    private String licensePlate;

    @Schema(description = "Marca/Modelo do veículo")
    private String model;

    @Schema(description = "UF de emplacamento")
    private String registrationState;

    @Schema(description = "Nome do credor")
    private String creditorName;

    @Schema(description = "Número do contrato")
    private String contractNumber;

    @Schema(description = "Etapas do fluxo para o veiculo", allowableValues = {
            "CERTIDAO_BUSCA_APREENSAO_EMITIDA", "BUSCA_PELO_VEICULO",
            "CERTIDAO_BUSCA_APREENSAO_RECEBIDA", "CERTIDAO_BUSCA_APREENSAO_RECEBIDA",
    })
    private VehicleStageEnum stage;

    @Schema(description = "Status do fluxo para o veículo", allowableValues = {
            "A_INICIAR", "LOCALIZADOR_ACIONADO", "VEICULO_NAO_LOCALIZADO",
            "VEICULO_LOCALIZADO", "GUINCHO_ACIONADO", "VEICULO_NAO_LOCALIZADO_GUINCHO",
            "VEICULO_RECOLHIDO_GUINCHO", "PATIO_ACIONADO", "VEICULO_NO_PATIO_INTERMEDIARIO",
            "VEICULO_NO_PATIO_FINAL",
    })
    private VehicleStatusEnum status;

    @Schema(description = "Data do pedido")
    private LocalDate requestDate;

    @Schema(description = "Data e hora da apreensão do veículo")
    private LocalDateTime vehicleSeizureDateTime;

    @Schema(description = "Data e hora da última movimentação")
    private LocalDateTime lastMovementDate ;

    @Schema(description = "Data e hora da apreensão agendada")
    private LocalDateTime seizureScheduledDate;

}
