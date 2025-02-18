package com.montreal.msiav_bh.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.montreal.core.domain.dto.FileDataDTO;
import com.montreal.msiav_bh.collection.VehicleImagesCollection;
import com.montreal.msiav_bh.dto.VehicleImagesDTO;
import com.montreal.msiav_bh.enumerations.VisionTypeEnum;

@Mapper
public interface IVehicleImagesMapper {

    IVehicleImagesMapper INSTANCE = Mappers.getMapper(IVehicleImagesMapper.class);

    default VehicleImagesCollection toEntity(FileDataDTO dto, VisionTypeEnum visionType,
                                             String vehicleId, String vehicleSeizureId) {

        VehicleImagesCollection entity = new VehicleImagesCollection();

        entity.setVehicleId(vehicleId);
        entity.setVehicleSeizureId(vehicleSeizureId);
        entity.setName(dto.getName());
        entity.setOriginalSize(dto.getOriginalSize());
        entity.setImageType(dto.getImageType());
        entity.setImageUrl(dto.getImageUrl());
        entity.setVisionType(visionType);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUploadedAt(LocalDateTime.now());

        return entity;
    }
    
    VehicleImagesDTO toDto(VehicleImagesCollection entity);

}
