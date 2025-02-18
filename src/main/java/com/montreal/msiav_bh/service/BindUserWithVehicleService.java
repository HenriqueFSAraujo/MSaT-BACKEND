package com.montreal.msiav_bh.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.montreal.msiav_bh.collection.UserVehicleAssociationCollection;
import com.montreal.msiav_bh.collection.VehicleCollection;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.repository.UserVehicleAssociationRepository;
import com.montreal.oauth.domain.entity.UserInfo;
import com.montreal.oauth.domain.enumerations.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BindUserWithVehicleService {

    private final VehicleService vehicleService;
    private final CompanyService companyService;
    private final UserMongoService userMongoService;
    private final UserVehicleAssociationRepository userVehicleAssociationRepository;
    
    public void bindUserAgenteOficialWithVehicleByCompany(String companyId, String vehicleId) {
        // Obtém a empresa
        CompanyDTO company = companyService.findById(companyId);
        log.info("Empresa encontrada: {}", company);
        

        // Obtém a lista de usuários da empresa
        List<UserInfo> usuarios = userMongoService.findUsersByCompanyId(companyId);

        // Valida se o veículo existe
        VehicleCollection vehicle = vehicleService.findById(vehicleId);
        if (vehicle == null) {
            log.error("Veículo com ID {} não encontrado.", vehicleId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo não encontrado.");
        }
        log.info("Veículo encontrado: {}", vehicle);
        
        UserInfo userLogged = userMongoService.getLoggedInUser();

        for (UserInfo user : usuarios) {
            try {
                // Verifica se o usuário é agente oficial

                if (!CompanyTypeEnum.DADOS_DETRAN.equals(company.getCompanyType())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empresa não é do tipo Agente Oficial");
                }

                if (!isAgenteOficial(user)) {
                    log.warn("Usuário {} não é um agente oficial. Pulando...", user.getId());
                    continue;
                    // Pula para o próximo usuário
                }

                // Verifica se já existe um vínculo
                if (userVehicleAssociationRepository.existsByUserIdAndVehicleId(user.getId(), vehicleId)) {
                    log.warn("Vínculo entre usuário {} e veículo {} já existe. Pulando...", user.getId(), vehicleId);
                    continue; // Pula para o próximo usuário
                }


                // Cria a associação
                UserVehicleAssociationCollection association = UserVehicleAssociationCollection.builder()
                        .userId(user.getId())
                        .vehicleId(vehicleId)
                        .associatedBy(userLogged.getId())
                        .createdAt(LocalDateTime.now())
                        .build();

                // Salva a associação no MongoDB
                userVehicleAssociationRepository.save(association);
                log.info("Vínculo entre usuário {} e veículo {} criado com sucesso.", user.getId(), vehicleId);

            } catch (ResponseStatusException ex) {
                log.error("Erro ao processar vínculo para o usuário {}: {}", user.getId(), ex.getMessage());
            } catch (Exception ex) {
                log.error("Erro inesperado ao vincular usuário {}: {}", user.getId(), ex.getMessage(), ex);
            }
        }
    }

    
    public String bindUserWithVehicleAgenteOficial(Long userId, String vehicleId, Long associatedBy) {
    	
    	VehicleCollection vehicle = vehicleService.findById(vehicleId);
    	UserInfo user = userMongoService.findById(userId);
        
    	// Valida se o usuário e o veículo existem
        validateUserAndVehicle(user, vehicle);

        boolean isAOf = isAgenteOficial(user);
        
        if(!isAOf) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é agente oficial.");
        }
        
        // Verifica se já existe um vínculo
        if (userVehicleAssociationRepository.existsByUserIdAndVehicleId(userId, vehicleId)) {
            log.warn("Vínculo entre usuário {} e veículo {} já existe.", userId, vehicleId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um vínculo entre este usuário e veículo.");
        }

        // Cria a associação
        UserVehicleAssociationCollection association = UserVehicleAssociationCollection.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .associatedBy(associatedBy)
                .createdAt(LocalDateTime.now())
                .build();

        // Salva a associação no MongoDB
        userVehicleAssociationRepository.save(association);
        log.info("Vínculo entre usuário {} e veículo {} criado com sucesso.", userId, vehicleId);

        return "Usuário vinculado ao veículo com sucesso.";
    }

    public String unbindUserWithVehicleAgenteOficial(Long userId, String vehicleId) {
    	
    	VehicleCollection vehicle = vehicleService.findById(vehicleId);
    	UserInfo user = userMongoService.findById(userId);
    	
        // Valida se o usuário e o veículo existem
    	 validateUserAndVehicle(user, vehicle);

        // Verifica se o vínculo existe
        var association = userVehicleAssociationRepository.findByUserIdAndVehicleId(userId, vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vínculo entre este usuário e veículo não encontrado."));

        // Remove o vínculo
        userVehicleAssociationRepository.delete(association);
        log.info("Vínculo entre usuário {} e veículo {} removido com sucesso.", userId, vehicleId);

        return "Vínculo entre usuário e veículo removido com sucesso.";
    }
    
    private void validateUserAndVehicle(UserInfo user, VehicleCollection vehicle) {
        // Verifica se o veículo existe
        if (vehicle == null) {
            log.error("Veículo com ID {} não encontrado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo não encontrado.");
        }
        
        log.info("Veículo encontrado: {}", vehicle);

        // Verifica se o usuário existe
        if (user == null) {
            log.error("Usuário com ID {} não encontrado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado.");
        }
        log.info("Usuário encontrado: {}", user);

    }
    
    private boolean isAgenteOficial(UserInfo user) {
        return user.getRoles().stream()
                .anyMatch(role -> RoleEnum.ROLE_AGENTE_OFICIAL.equals(role.getName()));
    }

}
