package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FileNotificationDTO {

    @JsonProperty("receivedDate")
    private LocalDate receivedDate;

    @JsonProperty("fileEvidence")
    private String fileEvidence;
}

