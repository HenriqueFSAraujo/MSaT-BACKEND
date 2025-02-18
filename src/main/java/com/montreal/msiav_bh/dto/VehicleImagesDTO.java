package com.montreal.msiav_bh.dto;

import com.montreal.msiav_bh.enumerations.VisionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleImagesDTO {

    private String id;
    private String vehicleId;
    private String vehicleSeizureId;
    private String name;
    private String originalSize;
    private String imageType;
    private String imageUrl;
    private VisionTypeEnum visionType;

}
