package com.montreal.msiav_bh.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.montreal.msiav_bh.enumerations.TypeHistoryEnum;

import lombok.Data;

@Data
public class HistoryDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("id_vehicle")
    private String idVehicle;

    @JsonProperty("license_plate")
    private String licensePlate;

    @JsonProperty("model")
    private String model;

    @JsonProperty("creditor_name")
    private String creditorName;

    @JsonProperty("contract_number")
    private String contractNumber;

    @JsonProperty("creation_date_time")
    private LocalDateTime creationDateTime;

    @JsonProperty("type_history")
    private TypeHistoryEnum typeHistory;

    @JsonProperty("location")
    private LocationDTO location;

    @JsonProperty("collected")
    private CollectedDTO collected;

    @JsonProperty("impound_lot")
    private ImpoundLotDTO impoundLot;
}