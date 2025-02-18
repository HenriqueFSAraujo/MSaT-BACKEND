package com.montreal.core.domain.service;

import com.montreal.core.domain.dto.FileDataDTO;
import com.montreal.core.domain.exception.FileException;
import com.montreal.core.domain.properties.FileProperties;
import com.montreal.msiav_bh.enumerations.VisionTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileSystemService {

    private final FileProperties fileProperties;

    public FileDataDTO processAndSaveFile(MultipartFile file, String vehicleId, VisionTypeEnum visionType) {
        log.info("Processando e salvando arquivo do veiculo {} - type: {}", vehicleId, visionType);

        try {

            var name = String.format("%s_%s", visionType, file.getOriginalFilename()) ;
            var uploadDir = fileProperties.getUploadDir();
            var filePath = Paths.get(uploadDir, vehicleId, name);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            log.info("Arquivo {} do veiculo {} - type: {} salvo no disco com sucesso", name, vehicleId, visionType);

            return FileDataDTO.builder()
                    .name(name)
                    .originalSize(file.getSize() + " bytes")
                    .imageType(file.getContentType())
                    .imageUrl(filePath.toString())
                    .build();

        } catch (Exception e) {
            throw new FileException(String.format("Erro ao salvar o arquivo do veiculo %s - Erro: %s",
                    vehicleId, e.getMessage()), e);
        }

    }

}
