package com.montreal.msiav_bh.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
	
    @Schema(description = "Código de Endereçamento Postal (CEP)")
    private String postalCode;

    @Schema(description = "Logradouro")
    private String street;

    @Schema(description = "Número do endereço")
    private String number;

    @Schema(description = "Bairro")
    private String neighborhood;

    @Schema(description = "Complemento do endereço")
    private String complement;

    @Schema(description = "Estado")
    private String state;

    @Schema(description = "Cidade")
    private String city;

    @Schema(description = "Ponto de referência")
    private String referencePoint;

    @Schema(description = "Observação adicional")
    private String note;
}
