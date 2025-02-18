package com.montreal.msiav_bh.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisionTypeEnum {

    FOTO_FRONTAL("Foto frontal do veículo"),
    FOTO_TRASEIRA("Foto traseira do veículo"),
    FOTO_LATERAL_ESQUERDA("Foto lateral esquerda do veículo"),
    FOTO_LATERAL_DIREITA("Foto lateral direita do veículo"),
    FOTO_INTERIOR("Foto do interior do veículo"),
    FOTO_MOTOR("Foto do motor do veículo");

    private final String description;
}