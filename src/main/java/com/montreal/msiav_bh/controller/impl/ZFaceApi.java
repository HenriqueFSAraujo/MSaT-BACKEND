package com.montreal.msiav_bh.controller.impl;

import com.montreal.msiav_bh.controller.IZFaceApi;
import com.montreal.msiav_bh.dto.request.ZFaceRequest;
import com.montreal.oauth.domain.dto.response.FacialBiometricResponse;
import com.montreal.zface.service.ZFaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ZFaceApi implements IZFaceApi {

    private final ZFaceService zFaceService;

    @Override
    public FacialBiometricResponse compareImage(ZFaceRequest zFaceRequest) {
        var score = zFaceService.compareImage(zFaceRequest.getBase64Image1(), zFaceRequest.getBase64Image2());
        return FacialBiometricResponse.builder().score(score).build();
    }

}
