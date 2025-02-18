package com.montreal.zface.client;

import com.montreal.core.domain.exception.ClientServiceException;
import com.montreal.zface.response.CompareImageResponse;
import com.montreal.zface.response.TokenResponse;
import com.montreal.zface.properties.ZFaceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ZFaceClient  {

    private static final String TOKEN_URL = "%s/Token";
    private static final String COMPARE_IMAGE_URL = "%s/Cognitive/CompareImage";

    private final ZFaceProperties zFaceProperties;
    private final RestTemplate restTemplate;

    public CompareImageResponse compareImage(String token, InputStream inputStream1, InputStream inputStream2) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(token);

            var body = getStringObjectMultiValueMap(inputStream1, inputStream2);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            var uri = String.format(COMPARE_IMAGE_URL, zFaceProperties.getUrl());
            var response = restTemplate.postForEntity(uri, requestEntity, CompareImageResponse.class);

            if (response.getStatusCode().isError()) {
                throw new ClientServiceException("Erro ao comparar imagens: " + response.getStatusCode());
            }

            return response.getBody();

        } catch (ClientServiceException e) {
           throw e;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new ClientServiceException("Erro ao comparar imagens.", e);
        }

    }

    public TokenResponse getToken(String username, String password) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(MediaType.parseMediaTypes("text/plain"));
            headers.add("Cookie", "cookiesession1=678A3E5BGHJKLMNOPQRSTUV012346A1E");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("Username", username);
            body.add("Password", password);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            String url = String.format(TOKEN_URL, zFaceProperties.getUrl());

            HttpEntity<TokenResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, TokenResponse.class);

            if (response.getBody() == null) {
                throw new ClientServiceException("Falha ao obter o token de autenticação.");
            }

            return response.getBody();

        } catch (ClientServiceException e) {
            throw e;

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new ClientServiceException("Erro ao obter o token de autenticação.", e);
        }

    }

    private MultiValueMap<String, Object> getStringObjectMultiValueMap(InputStream inputStream1, InputStream inputStream2) throws IOException {

        byte[] bytes1 = convertInputStreamToByteArray(inputStream1);
        byte[] bytes2 = convertInputStreamToByteArray(inputStream2);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("Scale", "");
        body.add("Enhance", "false");
        body.add("FastDetection", "false");
        body.add("Liveness", "false");
        body.add("Quality", "false");
        body.add("Async", "false");

        body.add("file", new ByteArrayResource(bytes1) {
            @Override
            public String getFilename() {
                return "file1.png";
            }
        });
        body.add("file2", new ByteArrayResource(bytes2) {
            @Override
            public String getFilename() {
                return "file2.png";
            }
        });
        return body;
    }

    private byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(temp, 0, temp.length)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

}
