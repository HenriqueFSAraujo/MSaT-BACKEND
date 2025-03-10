package com.montreal.msiav_rio.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataAuthResponse {

    private String token;

    @JsonProperty("expires_in")
    private String expiresIn;

}
