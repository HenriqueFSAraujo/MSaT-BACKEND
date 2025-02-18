package com.montreal.msiav_bh.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Etapa do veiculo
 */
@Getter
@AllArgsConstructor
public enum VehicleStageEnum {

    CERTIDAO_BUSCA_APREENSAO_EMITIDA("Certidão Busca Apreensão Emitida"),
    BUSCA_PELO_VEICULO("Busca pelo Veículo"),
    RECOLHIMENTO_DO_VEICULO("Recolhimento do Veículo"),
    VEICULO_RECOLHIDO("Veículo Recolhido"),
    AGENTE_OFICIAL_ACIONADO ("Agente Oficial Acionado");

    private final String description;

}
