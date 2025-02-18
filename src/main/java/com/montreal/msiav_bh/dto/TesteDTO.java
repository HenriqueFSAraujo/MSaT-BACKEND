package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TesteDTO {

    @JsonProperty("nome")
    private String nome; // Nome da pessoa

    @JsonProperty("idade")
    private Integer idade; // Idade da pessoa
}
