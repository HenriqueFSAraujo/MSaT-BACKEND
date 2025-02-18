package com.montreal.msiav_bh.service;

import java.time.LocalDateTime;

import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.domain.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.montreal.msiav_bh.collection.VehicleSeizureCollection;
import com.montreal.msiav_bh.dto.VehicleSeizureDTO;
import com.montreal.msiav_bh.mapper.IVehicleSeizureMapper;
import com.montreal.msiav_bh.repository.VehicleSeizureRepository;
import com.montreal.oauth.domain.enumerations.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleSeizureService {

    private final VehicleSeizureRepository vehicleSeizureRepository;
    private final UserMongoService userMongoService;
    private final VehicleService vehicleService;
    private final CompanyService companyService;
    private final AddressService addressService;

    /**
     * Lista todas as apreensões de veículos.
     *
     * @return Lista de apreensões em DTO
     */
    public Page<VehicleSeizureDTO> findAll(Pageable pageable) {
        log.info("Buscando todas as apreensões de veículos com paginação");

        try {
        	checkHasPermission();

            Page<VehicleSeizureCollection> seizures = vehicleSeizureRepository.findAll(pageable);
            return seizures.map(IVehicleSeizureMapper.INSTANCE::toDto);
        } catch (NegocioException e) {
            log.error("Erro de permissão ao buscar todas as apreensões: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar todas as apreensões: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar todas as apreensões de veículos.", e);
        }
    }

    /**
     * Busca uma apreensão de veículo por ID.
     *
     * @param id identificador da apreensão
     * @return DTO da apreensão encontrada
     */
    public VehicleSeizureDTO findById(String id) {
        log.info("Buscando apreensão de veículo com ID: {}", id);
        validateSeizureIdExists(id);

        try {
        	checkHasPermission();

            VehicleSeizureCollection seizure = vehicleSeizureRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Apreensão com ID " + id + " não encontrada"));

            return IVehicleSeizureMapper.INSTANCE.toDto(seizure);
        } catch (NegocioException | NotFoundException e) {
            log.error("Erro de permissão ao buscar apreensão com ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar apreensão com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar apreensão de veículo.", e);
        }
    }

    /**
     * Cria uma nova apreensão de veículo.
     *
     * @param dto dados da apreensão
     * @return DTO da apreensão criada
     */
    @Transactional
    public VehicleSeizureDTO create(VehicleSeizureDTO dto) {
        log.info("Criando uma nova apreensão de veículo");

        try {
        	checkHasPermission();

            if (dto.getSeizureDate().isAfter(LocalDateTime.now())) {
                throw new NegocioException("A data de apreensão não pode estar no futuro.");
            }
            
            vehicleService.validateVehicleIdExists(dto.getVehicleId());
            companyService.validateCompanyIdExists(dto.getCompanyId());
            addressService.validateAddressIdExists(dto.getAddressId());

            VehicleSeizureCollection seizure = IVehicleSeizureMapper.INSTANCE.toEntity(dto);
            
            seizure.setCreatedAt(LocalDateTime.now());
            
            VehicleSeizureCollection savedSeizure = vehicleSeizureRepository.save(seizure);
            log.info("Apreensão de veículo criada com sucesso com ID: {}", savedSeizure.getId());

            return IVehicleSeizureMapper.INSTANCE.toDto(savedSeizure);
        } catch (NegocioException e) {
            log.error("Erro de negócio ao criar apreensão: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar apreensão: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao criar apreensão de veículo.", e);
        }
    }

    /**
     * Atualiza uma apreensão de veículo por ID.
     *
     * @param id  identificador da apreensão
     * @param dto dados atualizados da apreensão
     * @return DTO da apreensão atualizada
     */
    @Transactional
    public VehicleSeizureDTO update(String id, VehicleSeizureDTO dto) {
        log.info("Atualizando a apreensão de veículo com ID: {}", id);

        try {
        	checkHasPermission();
        	validateSeizureIdExists(id);
            vehicleService.validateVehicleIdExists(dto.getVehicleId());
            companyService.validateCompanyIdExists(dto.getCompanyId());
            addressService.validateAddressIdExists(dto.getAddressId());

            VehicleSeizureCollection existingSeizure = vehicleSeizureRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Apreensão com ID " + id + " não encontrada"));

            existingSeizure.setUserId(dto.getUserId());
            existingSeizure.setVehicleId(dto.getVehicleId());
            existingSeizure.setAddressId(dto.getAddressId());
            existingSeizure.setCompanyId(dto.getCompanyId());
            existingSeizure.setVehicleCondition(dto.getVehicleCondition());
            existingSeizure.setSeizureDate(dto.getSeizureDate());
            existingSeizure.setDescription(dto.getDescription());

            VehicleSeizureCollection updatedSeizure = vehicleSeizureRepository.save(existingSeizure);
            log.info("Apreensão de veículo com ID {} atualizada com sucesso", id);

            return IVehicleSeizureMapper.INSTANCE.toDto(updatedSeizure);
        } catch (NegocioException | NotFoundException e) {
            log.error("Erro de negócio ao atualizar apreensão com ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar apreensão com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar apreensão de veículo.", e);
        }
    }

    /**
     * Exclui uma apreensão de veículo por ID.
     *
     * @param id identificador da apreensão
     */
    @Transactional
    public void delete(String id) {
        log.info("Excluindo a apreensão de veículo com ID: {}", id);

        try {
        	checkHasPermission();
        	validateSeizureIdExists(id);

            if (!vehicleSeizureRepository.existsById(id)) {
                throw new RuntimeException("Apreensão com ID " + id + " não encontrada");
            }

            vehicleSeizureRepository.deleteById(id);
            log.info("Apreensão de veículo com ID {} excluída com sucesso", id);
        } catch (NegocioException | NotFoundException e) {
            log.error("Erro de negócio ao excluir apreensão com ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir apreensão com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao excluir apreensão de veículo.", e);
        }
    }
    
    public void validateSeizureIdExists(String vehicleSeizureId) {
        log.info("Validando se a apreensão com ID {} existe.", vehicleSeizureId);
        if (!vehicleSeizureRepository.existsById(vehicleSeizureId)) {
            log.error("Apreensão com ID {} não encontrada.", vehicleSeizureId);
            throw new NotFoundException(String.format("Apreensão de veículo %s não encontrada.", vehicleSeizureId));
        }
    }
    
    private void checkHasPermission() {
    	var userInfo = userMongoService.getLoggedInUser();

        // Verifica se o usuário possui a role necessária
        boolean hasPermission = userInfo.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleEnum.ROLE_ADMIN || role.getName() == RoleEnum.ROLE_AGENTE_OFICIAL);

        if (!hasPermission) {
            throw new NegocioException("Usuário não possui permissão para realizar esta operação.");
        }
    }
}
