package com.montreal.msiav_bh.dto;

import com.montreal.msiav_bh.collection.AddressCollection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbableAddressDTO {

    @Schema(description = "Identificador único da associação de endereço provável")
    private String id;

    @Schema(description = "ID do veículo associado")
    private String vehicleId;

    @Schema(description = "Dados completos do endereço associado")
    private AddressCollection address;
}
