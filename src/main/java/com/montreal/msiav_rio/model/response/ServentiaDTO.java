package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServentiaDTO {

    private int cns;
    private String nome;
    private String endereco;
    private String titular;
    private String substituto;

    @JsonProperty("telefone_contato")
    private String telefoneContato;

}
