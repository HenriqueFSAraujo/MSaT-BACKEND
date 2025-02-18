package com.montreal.msiav_bh.collection;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EletronicNotificationCollection {

    @NotBlank
    private String communicationMethod;

    @NotBlank
    private String sentDate;

    @NotBlank
    private String readDate;

    @NotBlank
    private String fileEvidence;
}
