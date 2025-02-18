package com.montreal.msiav_bh.dto.request;

import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFilter {

    @Schema(description = "Tipo de empresa")
    private CompanyTypeEnum companyType;

    @Schema(description = "Nome da empresa")
    private String name;

    @Schema(description = "Documento da empresa")
    private String document;

}
