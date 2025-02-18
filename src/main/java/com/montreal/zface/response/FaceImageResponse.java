package com.montreal.zface.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceImageResponse {

    private Integer faceId;
    private boolean manualAnnotation;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Double confidence;
    private Double roll;
    private Double pitch;
    private Double yaw;
    private Double poseScore;
    private List<FaceLandmarkResponse> faceLandmark;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

}
