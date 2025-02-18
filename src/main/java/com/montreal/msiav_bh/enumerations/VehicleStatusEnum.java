package com.montreal.msiav_bh.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum para status do veiculo
 */
@Getter
@AllArgsConstructor
public enum VehicleStatusEnum {

    A_INICIAR("A iniciar","Veículo sem Localizador e Guincho"),
    LOCALIZADOR_ACIONADO("Localizador acionado","Localizador acionado"),
    VEICULO_NAO_LOCALIZADO("Veiculo não localizado","Localizador adicionou ao sistema que não encontrou veículo"),
    VEICULO_LOCALIZADO("Veiculo Localizado","Localizador adicionou endereço"),
    GUINCHO_ACIONADO("Guincho acionado","Guincho acionado"),
    VEICULO_NAO_LOCALIZADO_GUINCHO("Veiculo não localizado pelo Guincho","Guincho adicionou ao sistema que não encontrou o veículo"),
    VEICULO_RECOLHIDO_GUINCHO("Veiculo recolhido pelo Guincho","Guincho adicionou ao sistema data e hora de recolhimento"),
    PATIO_ACIONADO("Pátio acionado","Pátio acionado"),
    VEICULO_NO_PATIO_INTERMEDIARIO("Veículo no pátio intermediário","Pátio associado como intermediário adicionou ao sistema data e hora de entrega"),
    VEICULO_NO_PATIO_FINAL("Veículo no pátio final","Pátio associado como final adicionou ao sistema data e hora de entrega"),
    AGENTE_OFICIAL_ACIONADO ("Agente Oficial Acionado" ,"Veículo acionado para o Agente Oficial");

    private final String description;
    private final String action;

}
