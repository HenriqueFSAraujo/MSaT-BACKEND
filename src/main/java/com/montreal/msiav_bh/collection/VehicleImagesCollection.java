package com.montreal.msiav_bh.collection;


import com.montreal.msiav_bh.enumerations.VisionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "vehicle_images")
public class VehicleImagesCollection {

    @Id
    private String id;

    private String vehicleId;
    private String vehicleSeizureId;
    private String name;
    private String originalSize;
    private String currentSize;
    private String imageType;
    private String imageUrl;
    private VisionTypeEnum visionType;

    @UpdateTimestamp
    private LocalDateTime uploadedAt;

    @CreatedDate
    private LocalDateTime createdAt;

}
