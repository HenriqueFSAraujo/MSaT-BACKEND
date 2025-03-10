package com.montreal.msiav_bh.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeizureDateRequest {
	
    private String vehicleId;
    private LocalDateTime seizureDate;

}
