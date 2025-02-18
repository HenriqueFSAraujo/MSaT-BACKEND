package com.montreal.zface.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareImageResponse {

    private Integer status;
    private Integer requestType;
    private Integer userId;
    private double scale;
    private boolean enhance;
    private boolean fastDetection;
    private boolean liveness;
    private boolean quality;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private FaceImageResponse faceImage1;
    private FaceImageResponse faceImage2;
    private Double score;

}
