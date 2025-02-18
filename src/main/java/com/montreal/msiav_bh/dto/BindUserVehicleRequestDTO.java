package com.montreal.msiav_bh.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BindUserVehicleRequestDTO {
	
    @NotBlank(message = "O ID do usuário é obrigatório.")
    private String userId;

    @NotBlank(message = "O ID do veículo é obrigatório.")
    private String vehicleId;
}
