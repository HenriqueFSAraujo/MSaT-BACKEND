package com.montreal.msiav_bh.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VehicleConditionEnum {


    OTIMO("Ótimo estado de conservação"),
    BOM("Bom estado de conservação"),
    REGULAR("Estado de conservação regular"),
    RUIM("Ruim estado de conservação");

    private final String description;
}
