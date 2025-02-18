package com.montreal.msiav_bh.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.montreal.msiav_bh.collection.VehicleSeizureCollection;
import com.montreal.msiav_bh.dto.VehicleSeizureDTO;

@Mapper
public interface IVehicleSeizureMapper {

    IVehicleSeizureMapper INSTANCE = Mappers.getMapper(IVehicleSeizureMapper.class);

   // @Mapping(target = "images", ignore = true) // A lista de imagens será gerenciada separadamente no serviço
    VehicleSeizureDTO toDto(VehicleSeizureCollection entity);

    @Mapping(target = "id", ignore = true) // O ID será gerenciado pelo MongoDB
    VehicleSeizureCollection toEntity(VehicleSeizureDTO dto);
}
