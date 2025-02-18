package com.montreal.msiav_bh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
public class ReportDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("seizureDateId")
    private String seizureDateId;

    @JsonProperty("addressId")
    private String addressId;

    @JsonProperty("mandateNumber")
    private String mandateNumber;

    @JsonProperty("mandateDate")
    private LocalDate mandateDate;

    @JsonProperty("debtorId")
    private String debtorId;

    @JsonProperty("vehicleId")
    private String vehicleId;

    @JsonProperty("seizureAddress")
    private String seizureAddress;

    @JsonProperty("yardId")
    private String yardId;

    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("witnessesId")
    private String witnessesId;

    @JsonProperty("contractID")
    private String contractID;

    @JsonProperty("contractNumber")
    private String contractNumber;

    @JsonProperty("debtValue")
    private BigDecimal debtValue;

    @JsonProperty("towTruckId")
    private String towTruckId;

    @JsonProperty("vehicleImagesId")
    private String vehicleImagesId;

    @JsonProperty("notificationId")
    private String notificationId;

    @JsonProperty("arNotificationId")
    private String arNotificationId;

    @JsonProperty("debtor")
    private DebtorDTO debtor;

    @JsonProperty("userImagesId")
    private String userImagesId;

}