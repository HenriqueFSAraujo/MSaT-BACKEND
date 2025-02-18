package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoDTO {

    @JsonProperty("uf_emplacamento")
    private String ufEmplacamento;

    @JsonProperty("placa")
    private String placa;

    @JsonProperty("modelo")
    private String modelo;

    @JsonProperty("chassi")
    private String chassi;

    @JsonProperty("renavam")
    private String renavam;

    @JsonProperty("ano_fabricacao")
    private String anoFabricacao;

    @JsonProperty("ano_modelo")
    private String anoModelo;

    @JsonProperty("gravame")
    private String gravame;

    @JsonProperty("marca_modelo")
    private String marcaModelo;

    @JsonProperty("cor")
    private String cor;

    @JsonProperty("registro_detran")
    private String registroDetran;

    @JsonProperty("possui_gps")
    private String possuiGps;

}
