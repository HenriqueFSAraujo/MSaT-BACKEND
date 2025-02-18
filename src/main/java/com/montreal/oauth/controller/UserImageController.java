package com.montreal.oauth.controller;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.montreal.oauth.domain.dto.request.FacialBiometricRequest;
import com.montreal.oauth.domain.dto.response.FacialBiometricResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.montreal.oauth.domain.entity.UserImage;
import com.montreal.oauth.domain.service.UserImageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/api/v1/user-images")
@RequiredArgsConstructor
@Tag(name = "Imagem do Usuário")
public class UserImageController {

    public static final String CONTENT_TYPE_PNG = "image/png";
    public static final String CONTENT_TYPE_JPG = "image/jpg";
    public static final String CONTENT_TYPE_JPEG = "image/jpeg";

    public static final String MSG_ERROR_ALLOWED_FILE = "Apenas arquivos de imagem PNG, JPG e JPEG são permitidos.";
    public static final String MSG_SUCCESS_UPLOAD_FILE = "Imagem do usuário enviada com sucesso.";

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(CONTENT_TYPE_PNG, CONTENT_TYPE_JPG, CONTENT_TYPE_JPEG);

    private final UserImageService userImageService;

    @PostMapping(value = "/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {

        String fileType = ofNullable(file.getOriginalFilename()).map(this::getContentType).orElse("");
        if (!StringUtils.hasText(fileType) && !ALLOWED_FILE_TYPES.contains(fileType)) {

            return createResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "false", MSG_ERROR_ALLOWED_FILE);
        }

        userImageService.uploadUserImage(userId, file);
        return createResponse(HttpStatus.OK, "true", MSG_SUCCESS_UPLOAD_FILE);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) {
        Optional<UserImage> userImage = userImageService.getUserImage(userId);

        return userImage.map(image -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                        .contentType(MediaType.parseMediaType(image.getFileType()))
                        .body(image.getImageData()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserImage(@PathVariable Long userId) {
        userImageService.deleteUserImage(userId);
        return ResponseEntity.ok("Imagem do usuário removida com sucesso.");
    }


    @PostMapping(value = "/validate-facial-biometrics", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FacialBiometricResponse validateFacialBiometrics(@RequestBody FacialBiometricRequest request) {
        return userImageService.compareImage(request);
    }

    private String getContentType(String nameFile) {
        return "image/".concat(nameFile.substring(nameFile.lastIndexOf(".") + 1));
    }

    private ResponseEntity<Map<String, String>> createResponse(HttpStatus status, String success, String message) {
        return ResponseEntity
                .status(status)
                .body(Map.of("success", success, "message", message));
    }

}
