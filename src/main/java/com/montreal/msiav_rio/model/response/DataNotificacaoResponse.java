package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.montreal.msiav_bh.helper.MultiFormatDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataNotificacaoResponse {

    @JsonProperty("nome_credor")
    private String nomeCredor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    @JsonProperty("data_pedido")
    private LocalDateTime dataPedido;

    @JsonProperty("numero_contrato")
    private String numeroContrato;

    @JsonProperty("protocolo")
    private String protocolo;

    @JsonProperty("etapa")
    private String etapa;

    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    @JsonProperty("data_movimentacao")
    private LocalDateTime dataMovimentacao;

    @JsonProperty("veiculos")
    private List<VeiculoDTO> veiculos;


}
