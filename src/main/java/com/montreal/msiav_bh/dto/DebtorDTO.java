package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebtorDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("cpfCnpj")
    private String cpfCnpj;
}

