package com.montreal.msiav_rio.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoContratoRequest {

    @JsonProperty("nome_credor")
    private String nomeCredor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("data_pedido")
    private LocalDate dataPedido;

    @JsonProperty("numero_contrato")
    private String numeroContrato;

    @JsonProperty("uf_emplacamento")
    private String ufEmplacamento;

    @JsonProperty("placa")
    private String placa;

    @JsonProperty("etapa")
    private String etapa;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("data_movimento")
    private LocalDate dataMovimento;

}
