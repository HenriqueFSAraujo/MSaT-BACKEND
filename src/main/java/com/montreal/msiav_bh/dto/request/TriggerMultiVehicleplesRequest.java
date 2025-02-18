package com.montreal.msiav_bh.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TriggerMultiVehicleplesRequest {
	
	@NotEmpty(message = "A lista de vehicleIds não pode estar vazia")
	private Set<String> vehicleIds;
 
}
