package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevedorDTO {

    private String nome;

    @JsonProperty("cpf_cnpj")
    private String cpfCnpj;

    private List<EnderecoDTO> enderecos;

    @JsonProperty("contatos_email")
    private List<EmailDTO> emails;

    @JsonProperty("contatos_telefone")
    private List<TelefoneDTO> telefones;

}
