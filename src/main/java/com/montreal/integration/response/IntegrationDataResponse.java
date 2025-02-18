package com.montreal.integration.response;

import java.util.List;

import com.montreal.msiav_bh.dto.AddressDTO;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.HistoryDTO;
import com.montreal.msiav_bh.dto.response.VehicleResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationDataResponse {

    @Schema(description = "Dados do veículo")
    private VehicleResponse vehicleData;

    @Schema(description = "Endereços associados ao veículo")
    private List<AddressDTO> addresses;

    @Schema(description = "Empresas associadas ao veículo")
    private List<CompanyDTO> companies;

    @Schema(description = "Históricos do veículo")
    private List<HistoryDTO> histories;

}
