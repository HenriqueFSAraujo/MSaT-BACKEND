package com.montreal.msiav_bh.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.enumerations.PhoneTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("document")
    private String document;

    @JsonProperty("phoneDDD")
    private String phoneDDD;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("phoneType")
    private PhoneTypeEnum phoneType;

    @JsonProperty("address")
    private AddressDTO address;

    @JsonProperty("name_responsible")
    private String nameResponsible;

    @JsonProperty("company_type")
    private CompanyTypeEnum companyType;

    @JsonProperty("isActive")
    public boolean isActive;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
