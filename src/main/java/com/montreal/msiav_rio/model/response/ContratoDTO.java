package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContratoDTO {

    @JsonProperty("numero")
    private String numero;

    @JsonProperty("protocolo")
    private String protocolo;

    @JsonProperty("data_contrato")
    private LocalDate dataContrato;

    @JsonProperty("data_pedido")
    private String dataPedido;

    @JsonProperty("data_notificacao")
    private LocalDateTime dataNotificacao;

    @JsonProperty("data_decurso_prazo")
    private LocalDate dataDecursoPrazo;

    @JsonProperty("municipio_contrato")
    private String municipioContrato;

    @JsonProperty("certidao_busca_apreensao")
    private String certidaoBuscaApreensao;

    @JsonProperty("valor_divida")
    private String valorDivida;

    @JsonProperty("valor_leilao")
    private String valorLeilao;

    @JsonProperty("taxa_juros")
    private String taxaJuros;

    @JsonProperty("valor_parcela")
    private String valorParcela;

    @JsonProperty("quantidade_parcelas_pagas")
    private Integer quantidadeParcelasPagas;

    @JsonProperty("quantidade_parcelas_abertas")
    private Integer quantidadeParcelasAbertas;

    @JsonProperty("data_primeira_parcela")
    private LocalDate dataPrimeiraParcela;

    @JsonProperty("descricao")
    private String descricao;

    private LocalDateTime dataApreensao;
}
