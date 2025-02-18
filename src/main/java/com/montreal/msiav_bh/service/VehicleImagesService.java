package com.montreal.msiav_bh.service;

import java.util.List;
import java.util.stream.Collectors;

import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.domain.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.montreal.core.domain.exception.FileException;
import com.montreal.core.domain.service.FileSystemService;
import com.montreal.msiav_bh.collection.VehicleImagesCollection;
import com.montreal.msiav_bh.dto.VehicleImagesDTO;
import com.montreal.msiav_bh.dto.VehicleSeizureDTO;
import com.montreal.msiav_bh.enumerations.VisionTypeEnum;
import com.montreal.msiav_bh.mapper.IVehicleImagesMapper;
import com.montreal.msiav_bh.repository.VehicleImagesRepository;
import com.montreal.oauth.domain.enumerations.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleImagesService {

    private final VehicleImagesRepository repository;
    private final FileSystemService fileSystemService;
    private final UserMongoService userMongoService;
    private final VehicleService vehicleService;
    private final VehicleSeizureService vehicleSeizureService;
    
    /**
     * Salva múltiplas imagens associadas a uma apreensão de veículo.
     */
    public List<VehicleImagesDTO> saveImages(String vehicleSeizureId, List<MultipartFile> imageFiles, List<String> visionTypes) {
    	log.info("Salvando {} imagens para a apreensão com ID: {}", imageFiles.size(), vehicleSeizureId);

        VehicleSeizureDTO vehicleSeizureDTO = vehicleSeizureService.findById(vehicleSeizureId);
        String vehicleId = vehicleSeizureDTO.getVehicleId();

        return imageFiles.stream()
                .map(file -> {
                    int index = imageFiles.indexOf(file);
                    VisionTypeEnum visionType = convertToVisionTypeEnum(visionTypes.get(index));
                    return save(file, visionType, vehicleId, vehicleSeizureId);
                })
                .collect(Collectors.toList());
    }

    public VehicleImagesDTO save(MultipartFile file, VisionTypeEnum visionType, String vehicleId, String vehicleSeizureId) {

        log.info("Salvando imagem do veiculo: {} - Tipo: {} - Apreensão: {}", vehicleId, visionType, vehicleSeizureId);

        try {
        	validateUserPermission();
        	vehicleSeizureService.validateSeizureIdExists(vehicleSeizureId);
        	vehicleService.validateVehicleIdExists(vehicleId);
        	
        	// Verificar se o arquivo é uma imagem válida (png, jpeg, jpg)
        	validateImageFileType(file);
        	
            var dataFile = fileSystemService.processAndSaveFile(file, vehicleId, visionType);

            var entity = IVehicleImagesMapper.INSTANCE.toEntity(dataFile, visionType, vehicleId, vehicleSeizureId);
            var entitySaved = repository.save(entity);

            var vehicleImages = VehicleImagesDTO.builder()
                    .id(entitySaved.getId())
                    .vehicleId(vehicleId)
                    .vehicleSeizureId(vehicleSeizureId)
                    .name(dataFile.getName())
                    .originalSize(dataFile.getOriginalSize())
                    .imageType(dataFile.getImageType())
                    .imageUrl(dataFile.getImageUrl())
                    .visionType(visionType)
                    .build();

            log.info("Imagem do veiculo: {} - Tipo: {} - Apreensão: {} salva com sucesso",
                    vehicleId, visionType, vehicleSeizureId);

            return vehicleImages;

        } catch (FileException e) {
            log.error("Erro ao salvar arquivo: {}", e.getMessage(), e);
            throw new NegocioException(
                    String.format("Erro ao salvar o arquivo do veículo %s - Problema: %s", vehicleId, e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar arquivo: {}", e.getMessage(), e);
            throw new NegocioException(String.format("Erro inesperado ao salvar o arquivo do veículo %s  - Problema: %s", vehicleId, e));
        }
    }
    
    public Page<VehicleImagesDTO> findAllBySeizureId(String vehicleSeizureId, Pageable pageable) {
        log.info("Buscando imagens para a apreensão com ID: {}", vehicleSeizureId);

        try {
            validateUserPermission();
            vehicleSeizureService.validateSeizureIdExists(vehicleSeizureId);

            Page<VehicleImagesCollection> imagesPage = repository.findByVehicleSeizureId(vehicleSeizureId, pageable);
            return imagesPage.map(IVehicleImagesMapper.INSTANCE::toDto);
        } catch (Exception e) {
            log.error("Erro ao buscar imagens da apreensão: {}", vehicleSeizureId, e);
            throw new NegocioException(String.format("Erro ao buscar imagens da apreensão - Problema: %s", e));
        }
    }

    public void deleteImage(String imageId) {
        log.info("Excluindo imagem com ID: {}", imageId);

        try {
            validateUserPermission();
            
            if (!repository.existsById(imageId)) {
                throw new NotFoundException(String.format("Imagem %s não encontrada para exclusão.", imageId));
            }

            repository.deleteById(imageId);

            log.info("Imagem com ID {} excluída com sucesso.", imageId);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao excluir imagem com ID: {}", imageId, e);
            throw new NegocioException(String.format("Erro ao excluir imagem  - Problema: %s", e.getMessage()));
        }
    }
    
    private VisionTypeEnum convertToVisionTypeEnum(String visionTypeString) {
        try {
            return VisionTypeEnum.valueOf(visionTypeString);
        } catch (IllegalArgumentException e) {
            log.error("Tipo de visão inválido recebido: {}", visionTypeString);
            throw new NegocioException(String.format("Tipo de visão inválido: %s. Valores permitidos: %s", visionTypeString, List.of(VisionTypeEnum.values())));
        }
    }
    
    private void validateUserPermission() {
        var userInfo = userMongoService.getLoggedInUser();
        boolean hasPermission = userInfo.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleEnum.ROLE_ADMIN || role.getName() == RoleEnum.ROLE_AGENTE_OFICIAL);

        if (!hasPermission) {
            log.error("Usuário não possui permissão para realizar esta operação.");
            throw new NegocioException("Usuário não possui permissão para realizar esta operação.");
        }
    }
    
    private void validateImageFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new NegocioException("O arquivo enviado não possui um nome válido.");
        }

        // Converte a extensão para letras minúsculas
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // Lista de extensões permitidas
        List<String> allowedExtensions = List.of("jpeg", "jpg", "png", "heic");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new NegocioException(String.format("Formato de arquivo inválido: %s. Apenas %s são permitidos.", fileExtension, allowedExtensions));
        }
    }

}
