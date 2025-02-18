package com.montreal.msiav_bh.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompanyTypeEnum {

    DADOS_ESCRITORIO_COBRANCA("DADOS_ESCRITORIO_COBRANCA", "Dados Escritório de Cobrança"),
    DADOS_LOCALIZADOR("DADOS_LOCALIZADOR", "Dados Localizador"),
    DADOS_GUINCHO("DADOS_GUINCHO", "Dados Guincho"),
    DADOS_PATIO("DADOS_PATIO", "Dados Pátio"),
    DADOS_DETRAN("DADOS_DETRAN", "Dados DETRAN");

    private final String code;
    private final String description;

    public static CompanyTypeEnum fromCode(String code) {
        for (CompanyTypeEnum type : CompanyTypeEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Código inválido para o tipo de empresa: " + code);
    }
}
