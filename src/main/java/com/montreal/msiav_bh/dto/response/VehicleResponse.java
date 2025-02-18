package com.montreal.msiav_bh.dto.response;

import com.montreal.msiav_bh.enumerations.VehicleStageEnum;
import com.montreal.msiav_bh.enumerations.VehicleStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VehicleResponse {

    @Schema(description = "Identificador único do registro do veículo")
    private String id;

    @Schema(description = "Placa do veículo")
    private String licensePlate;

    @Schema(description = "Modelo do veículo")
    private String model;

    @Schema(description = "UF de emplacamento")
    private String registrationState;

    @Schema(description = "Nome do credor associado ao veículo")
    private String creditorName;

    @Schema(description = "Número do contrato associado ao veículo")
    private String contractNumber;

    @Schema(description = "Etapa atual do veículo", allowableValues = {
            "CERTIDAO_BUSCA_APREENSAO_EMITIDA", "BUSCA_PELO_VEICULO",
            "RECOLHIMENTO_DO_VEICULO", "VEICULO_RECOLHIDO"
    })
    private VehicleStageEnum stage;

    @Schema(description = "Status atual do veículo", allowableValues = {
            "A_INICIAR", "LOCALIZADOR_ACIONADO", "VEICULO_NAO_LOCALIZADO", "VEICULO_LOCALIZADO",
            "GUINCHO_ACIONADO", "VEICULO_NAO_LOCALIZADO_GUINCHO", "VEICULO_RECOLHIDO_GUINCHO",
            "PATIO_ACIONADO", "VEICULO_NO_PATIO_INTERMEDIARIO", "VEICULO_NO_PATIO_FINAL"
    })
    private VehicleStatusEnum status;

    @Schema(description = "Data do pedido")
    private LocalDate requestDate;

    @Schema(description = "Data e hora da apreensão do veículo")
    private LocalDateTime vehicleSeizureDateTime;

    @Schema(description = "Data da última movimentação do veículo")
    private LocalDateTime lastMovementDate;

    @Schema(description = "Data e hora agendada para a apreensão do veículo")
    private LocalDateTime seizureScheduledDate;

}
