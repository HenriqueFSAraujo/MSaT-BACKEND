package com.montreal.zface.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceLandmarkResponse {

    private int x;
    private int y;
    private String type;

}
