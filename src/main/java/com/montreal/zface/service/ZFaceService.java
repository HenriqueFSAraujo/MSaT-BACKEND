package com.montreal.zface.service;

import com.montreal.core.domain.exception.CheckImageException;
import com.montreal.core.domain.exception.ClientServiceException;
import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.exception_handler.ProblemType;
import com.montreal.msiav_bh.service.SystemParameterService;
import com.montreal.zface.client.ZFaceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZFaceService {

    public static final String SYSTEM_NAME = "ZFace";
    public static final String PARAMETER_USER = "USER";
    public static final String PARAMETER_PASSWORD = "PASSWORD";
    public static final String PARAMETER_SCORE_ACCEPTED = "SCORE_ACCEPTED";

    private final ZFaceClient zFaceClient;
    private final SystemParameterService systemParameterService;

    public Double compareImage(String base64Image1, String base64Image2) {
        log.info("Comparing images");

        try {

            var scoredParameterized = systemParameterService.getSystemParameter(SYSTEM_NAME, PARAMETER_SCORE_ACCEPTED);
            log.info("Scored parameterized: {}", scoredParameterized.getValue());

            var token = generateToken();

            var filePath2 = convertBase64ToInputStream(base64Image2);
            var filePath1 = convertBase64ToInputStream(base64Image1);

            var response = zFaceClient.compareImage(token, filePath1, filePath2);

            if(response.getScore() >= Double.parseDouble(scoredParameterized.getValue())) {
                log.info("Images are similar - score: {}", response.getScore());

            } else {
                log.info("Images are not similar - score: {}", response.getScore());
                throw new CheckImageException(String.format("Imagens não estão no formato válido - score: %s", response.getScore()));
            }

            return response.getScore();

        } catch (ClientServiceException | CheckImageException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error comparing images", e);
            throw new NegocioException(ProblemType.ERRO_NEGOCIO, "Erro ao comparar imagens", e);
        }

    }


    private String generateToken() {

        var userApi = systemParameterService.getSystemParameter(SYSTEM_NAME, PARAMETER_USER);
        var passApi = systemParameterService.getSystemParameter(SYSTEM_NAME, PARAMETER_PASSWORD);

        return zFaceClient.getToken(userApi.getValue(), passApi.getValue()).getAccessToken();

    }

    public static InputStream convertBase64ToInputStream(String base64Content) {

        if (base64Content.contains(",")) {
            base64Content = base64Content.split(",")[1];
        }

        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
        return new ByteArrayInputStream(decodedBytes);
    }

}
