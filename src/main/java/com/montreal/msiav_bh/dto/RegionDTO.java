package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("sigla")
    private String acronym;

    @JsonProperty("nome")
    private String name;

}
