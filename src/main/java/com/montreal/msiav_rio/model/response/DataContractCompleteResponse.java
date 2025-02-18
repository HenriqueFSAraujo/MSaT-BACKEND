package com.montreal.msiav_rio.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.response.HistoryLocationInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContractCompleteResponse {

    private CredorDTO credor;
    private List<DevedorDTO> devedores;
    private List<GarantidorDTO> garantidores;
    private List<VeiculoDTO> veiculos;
    private ContratoDTO contrato;
    private ServentiaDTO serventia;
    private List<OrgaoEscobDTO> orgaoEscob;
    private List<OrgaoGuinchoDTO> orgaoGuincho;
    private List<OrgaoDespachanteDTO> orgaoDespachante;
    private List<OrgaoLeilaoDTO> orgaoLeilao;
    private List<OrgaoLocalizadorDTO> orgaoLocalizador;
    private List<OrgaoPatioDTO> orgaoPatio;

    private List<CompanyDTO> empresas;
    private List<HistoryLocationInfo> historicos;

}
