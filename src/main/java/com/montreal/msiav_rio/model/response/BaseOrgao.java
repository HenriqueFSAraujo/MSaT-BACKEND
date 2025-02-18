package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseOrgao {

    private String nome;
    private String cnpj;
    private String endereco;
    private String email;

    @JsonProperty("telefone_contato")
    private String telefoneContato;

}
