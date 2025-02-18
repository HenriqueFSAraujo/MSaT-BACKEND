package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemParameterDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("system")
    private String system;

    @JsonProperty("parameter")
    private String parameter;

    @JsonProperty("value")
    private String value;
}
