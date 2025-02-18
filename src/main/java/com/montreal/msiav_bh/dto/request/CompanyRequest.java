package com.montreal.msiav_bh.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.enumerations.PhoneTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
public class CompanyRequest {

    @NotBlank
    @Schema(description = "nome da empresa")
    private String name;

    @NotBlank
    @Schema(description = "email da empresa")
    private String email;

    @NotBlank
    @Schema(description = "documento da empresa")
    private String document;

    @NotBlank
    @Schema(description = "DDD do telefone")
    private String phoneDDD;
    
    @NotBlank
    @Schema(description = "Número do telefone")
    private String phoneNumber;

    @NotBlank
    @Schema(description = "Tipo de telefone")
    private PhoneTypeEnum phoneType;

    @NotNull
    @Valid
    @Schema(description = "Endereço da empresa")
    private AddressRequest address;

    @NotBlank
    @Schema(description = "Nome do responsável")
    private String nameResponsible;

    @NotNull
    @Schema(description = "Tipo de empresa", allowableValues = {
            "DADOS_ESCRITORIO_COBRANCA", "DADOS_LOCALIZADOR",
            "DADOS_GUINCHO", "DADOS_PATIO"
    })
    private CompanyTypeEnum companyType;

}
