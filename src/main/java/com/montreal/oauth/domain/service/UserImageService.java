package com.montreal.oauth.domain.service;

import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.oauth.domain.dto.request.FacialBiometricRequest;
import com.montreal.oauth.domain.dto.response.FacialBiometricResponse;
import com.montreal.oauth.domain.entity.UserImage;
import com.montreal.oauth.domain.repository.UserImageRepository;
import com.montreal.oauth.domain.repository.IUserRepository;
import com.montreal.zface.service.ZFaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserImageService {

    private final UserImageRepository userImageRepository;
    private final IUserRepository userRepository;
    private final ZFaceService zFaceService;

    public void uploadUserImage(Long userId, MultipartFile file)  {

        try {

            UserImage userImage = new UserImage();
            userImage.setImageData(file.getBytes());
            userImage.setFileType(file.getContentType());
            userImage.setFileName(file.getOriginalFilename());
            userImage.setIdUser(userId);

            getUserImage(userId).ifPresent(userImageRepository::delete);

            userImageRepository.save(userImage);

        } catch (Exception e) {
            log.error("Erro ao salvar imagem do usuário", e);
            throw new NegocioException("Erro ao salvar imagem do usuário");
        }

    }

    public Optional<UserImage> getUserImage(Long userId) {
        return userImageRepository.findByIdUser(userId);
    }

    public void deleteUserImage(Long userId) {
        getUserImage(userId).ifPresent(userImageRepository::delete);
    }

    public FacialBiometricResponse compareImage(FacialBiometricRequest request) {
        log.info("Validando imagem do usuário {}", request.getUserId());

        var userInfo = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com o ID fornecido."));

        var userImage = userImageRepository.findByIdUser(userInfo.getId())
                .orElseThrow(() -> new NotFoundException("Imagem do usuário não encontrada."));

        var imageBase64Saved = getImageBase64Saved(request, userImage);

        var score = zFaceService.compareImage(imageBase64Saved, request.getImageBase64());

        log.info("Imagem do usuário validada - score: {}", score);

        return FacialBiometricResponse.builder()
                .success(true)
                .score(score)
                .userId(request.getUserId())
                .build();

    }

    private String getImageBase64Saved(FacialBiometricRequest request, UserImage userImage) {
        if (request.getImageBase64().contains(",")) {
            request.setImageBase64(request.getImageBase64().split(",")[1]);

            return Base64.getEncoder().encodeToString(userImage.getImageData());
        }
        throw new NegocioException("Imagem não está no formato base64.");

    }

}
