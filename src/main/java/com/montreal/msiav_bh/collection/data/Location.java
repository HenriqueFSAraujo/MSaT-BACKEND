package com.montreal.msiav_bh.collection.data;

import com.montreal.msiav_bh.dto.request.AddressRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {


    private boolean vehicleFound;

    private String note;

    private AddressRequest address;

}
