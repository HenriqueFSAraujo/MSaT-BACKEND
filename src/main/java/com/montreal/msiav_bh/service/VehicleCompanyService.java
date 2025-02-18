package com.montreal.msiav_bh.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.collection.VehicleCollection;
import com.montreal.msiav_bh.collection.VehicleCompanyCollection;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.VehicleCompanyDTO;
import com.montreal.msiav_bh.dto.response.VehicleResponse;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.mapper.ICompanyMapper;
import com.montreal.msiav_bh.mapper.IVehicleMapper;
import com.montreal.msiav_bh.repository.CompanyRepository;
import com.montreal.msiav_bh.repository.VehicleCompanyRepository;
import com.montreal.msiav_bh.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleCompanyService {

    private final VehicleRepository vehicleRepository;
    private final CompanyRepository companyRepository;
    private final VehicleCompanyRepository vehicleCompanyRepository;
    private final ICompanyMapper companyMapper;
    private final IVehicleMapper vehicleMapper;
    private final BindUserWithVehicleService bindUserWithVehicleService;

    public void saveVehicleCompany(String vehicleId, String companyId, CompanyTypeEnum expectedCompanyType) {
        log.info("Salvando relacionamento entre veículo e empresa. VeículoID={}, EmpresaID={}", vehicleId, companyId);

        // Verificar se a empresa existe
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada com ID: " + companyId));

        // Verificar se o tipo da empresa está correto
        if (!company.getCompanyType().equals(expectedCompanyType)) {
            log.error("Tipo de empresa incorreto para EmpresaID={}. Esperado: {}, Encontrado: {}",
                    companyId, expectedCompanyType, company.getCompanyType());
            throw new IllegalArgumentException("O tipo da empresa " + company.getCompanyType()
                    + " não corresponde ao tipo esperado " + expectedCompanyType + ".");
        }

        // Verificar se o veículo existe
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado com ID: " + vehicleId));

        // Verificar se já existe uma empresa vinculada ao veículo com o mesmo tipo
        List<VehicleCompanyCollection> existingLinks = vehicleCompanyRepository.findVehicleCompaniesByVehicleId(vehicleId);
        for (VehicleCompanyCollection vc : existingLinks) {
            var existingCompany = companyRepository.findById(vc.getCompanyId())
                    .orElseThrow(() -> new NotFoundException("Empresa não encontrada com ID: " + vc.getCompanyId()));
            if (existingCompany.getCompanyType().equals(expectedCompanyType)) {
                log.error("Já existe uma empresa do tipo {} vinculada ao veículo {}", expectedCompanyType, vehicleId);
                throw new IllegalArgumentException("Já existe uma empresa do tipo " + expectedCompanyType
                        + " vinculada a este veículo.");
            }
        }

        // Verificar se a associação já existe
        Optional<VehicleCompanyCollection> existingVehicleCompany = vehicleCompanyRepository.findByVehicleIdAndCompanyId(vehicleId, companyId);

        if (existingVehicleCompany.isPresent()) {
            // Atualiza o vínculo existente
            VehicleCompanyCollection vehicleCompany = existingVehicleCompany.get();
            vehicleCompany.setCreatedAt(LocalDateTime.now());
            vehicleCompanyRepository.save(vehicleCompany);
            log.info("Vínculo atualizado com sucesso para VeículoID={} e EmpresaID={}", vehicleId, companyId);
        } else {
            // Cria um novo vínculo
        	var vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("Veículo não encontrado com ID: " + vehicleId));
            vehicle.setLastMovementDate(LocalDateTime.now()); // Atualiza a data de última movimentação
            vehicleRepository.save(vehicle); // Salva o veículo com a nova data
            vehicleCompanyRepository.save(VehicleCompanyCollection.builder()
                    .vehicleId(vehicleId)
                    .companyId(companyId)
                    .createdAt(LocalDateTime.now())
                    .build());

            log.info("Vínculo criado com sucesso entre VeículoID={} e EmpresaID={}", vehicleId, companyId);
        }
        bindUserWithVehicleService.bindUserAgenteOficialWithVehicleByCompany(companyId, vehicleId);
    }


    public void deleteVehicleCompany(String vehicleId, String companyId) {
        log.info("Deletando relacionamento entre veiculo e empresa");

        var vehicleCompany = vehicleCompanyRepository.findByVehicleIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new NotFoundException("Relacionamento não encontrado"));

        vehicleCompanyRepository.delete(vehicleCompany);
    }

    public List<VehicleCompanyDTO> findByVehicleId(String vehicleId) {
        log.info("Buscando empresas do veículo {}", vehicleId);

        // Busca todas as associações de VehicleCompany para o vehicleId
        List<VehicleCompanyCollection> vehicleCompanies = vehicleCompanyRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new NotFoundException("Não foram encontradas empresas para o veículo: " + vehicleId));

        if (vehicleCompanies.isEmpty()) {
            log.warn("Nenhuma associação encontrada para o veículo {}", vehicleId);
            return List.of();
        }

        // Busca os dados do veículo
        VehicleResponse vehicleResponse = vehicleRepository.findById(vehicleId)
                .map(vehicleMapper::toVehicleResponse)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado com ID: " + vehicleId));

        // Converte cada VehicleCompany para VehicleCompanyDTO
        return vehicleCompanies.stream()
                .map(vehicleCompany -> {
                    try {
                        // Busca os dados da empresa associados
                        CompanyDTO companyResponse = companyRepository.findById(vehicleCompany.getCompanyId())
                                .map(companyMapper::toDto)
                                .orElseThrow(() -> new NotFoundException(
                                        "Empresa não encontrada com ID: " + vehicleCompany.getCompanyId()));

                        // Retorna o DTO construído
                        return VehicleCompanyDTO.builder()
                                .id(vehicleCompany.getId())
                                .vehicleId(vehicleCompany.getVehicleId())
                                .companyId(vehicleCompany.getCompanyId())
                                .vehicle(vehicleResponse)
                                .company(companyResponse)
                                .createdAt(vehicleCompany.getCreatedAt())
                                .build();
                    } catch (NotFoundException e) {
                        log.error("Erro ao processar VehicleCompany com ID {}: {}", vehicleCompany.getId(), e.getMessage());
                        throw e; // Propaga a exceção para o chamador
                    }
                })
                .toList();
    }



    public void trigger(String companyId, String vehicleId) {
        log.info("Disparando evento para empresa {}", companyId);

        var vehicleCompany = vehicleCompanyRepository.findByVehicleIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new NotFoundException("Relacionamento não encontrado"));

        vehicleCompany.setDateTrigger(LocalDateTime.now());
        vehicleCompanyRepository.save(vehicleCompany);

    }

    /**
     * Cria vínculos entre uma empresa e uma lista de veículos.
     *
     * @param companyId  ID da empresa.
     * @param vehicleIds Conjunto de IDs dos veículos.
     */
    public void triggerMultiple(String companyId, Set<String> vehicleIds) {
        log.info("Iniciando criação de vínculos para a empresa {} com veículos {}", companyId, vehicleIds);

        try {
            // Verificar se a empresa existe
            companyRepository.findById(companyId).orElseThrow(() -> new NotFoundException("Empresa não encontrada: " + companyId));
            log.info("Empresa {} verificada com sucesso", companyId);

            // Iterar sobre cada vehicleId para validação e criação do vínculo
            for (String vehicleId : vehicleIds) {
                log.info("Processando veículo id|: {}", vehicleId);

                try {
                    // Verificar se o veículo existe
                    vehicleRepository.findById(vehicleId).orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + vehicleId));
                    log.info("Veículo {} verificado com sucesso", vehicleId);

                    // Criar novo vínculo entre veículo e empresa
                    VehicleCompanyCollection newLink = VehicleCompanyCollection.builder()
                            .vehicleId(vehicleId)
                            .companyId(companyId)
                            .createdAt(LocalDateTime.now())
                            .dateTrigger(LocalDateTime.now())
                            .build();

                    vehicleCompanyRepository.save(newLink);
                    log.info("Vínculo criado com sucesso entre EmpresaID={} e VeículoID={}", companyId, vehicleId);

                } catch (NotFoundException e) {
                    log.error("Erro de validação para veículo {}: {}", vehicleId, e.getMessage());
                    continue;
                } catch (DuplicateKeyException dkEx) {
                    log.warn("Vínculo já existe entre EmpresaID={} e VeículoID={}", companyId, vehicleId);
                    continue;
                } catch (Exception e) {
                    log.error("Erro inesperado ao criar vínculo para veículo {}: {}", vehicleId, e.getMessage(), e);
                    continue;
                }
            }

            log.info("Vínculos foram processados para a empresa {} e veículos {}", companyId, vehicleIds);

        } catch (NotFoundException e) {
            log.error("Erro de validação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar vínculos para a empresa {}: {}", companyId, e.getMessage(), e);
            throw new RuntimeException("Falha ao criar vínculos", e);
        }
    }
    
    public void triggerMultiplePatioVehicle(String impoundLotId, Set<String> vehicleIds) {
        log.info("Iniciando criação de vínculos para o pátio {} com veículos {}", impoundLotId, vehicleIds);

        try {
            // Verificar se o pátio existe
            companyRepository.findById(impoundLotId).orElseThrow(() -> new NotFoundException("Pátio não encontrado: " + impoundLotId));
            log.info("Pátio {} verificado com sucesso", impoundLotId);

            // Iterar sobre cada vehicleId para validação e criação do vínculo
            for (String vehicleId : vehicleIds) {
                log.info("Processando veículo id: {}", vehicleId);

                try {
                    // Verificar se o veículo existe
                    vehicleRepository.findById(vehicleId).orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + vehicleId));
                    log.info("Veículo {} verificado com sucesso", vehicleId);

                    // Criar novo vínculo entre veículo e pátio
                    VehicleCompanyCollection newLink = VehicleCompanyCollection.builder()
                            .vehicleId(vehicleId)
                            .companyId(impoundLotId)
                            .createdAt(LocalDateTime.now())
                            .dateTrigger(LocalDateTime.now())
                            .build();

                    vehicleCompanyRepository.save(newLink);
                    log.info("Vínculo criado com sucesso entre PatioID={} e VeículoID={}", impoundLotId, vehicleId);

                } catch (NotFoundException e) {
                    log.error("Erro de validação para veículo {}: {}", vehicleId, e.getMessage());
                    continue;
                } catch (DuplicateKeyException dkEx) {
                    log.warn("Vínculo já existe entre PatioID={} e VeículoID={}", impoundLotId, vehicleId);
                    continue;
                } catch (Exception e) {
                    log.error("Erro inesperado ao criar vínculo para veículo {}: {}", vehicleId, e.getMessage(), e);
                    continue;
                }
            }

            log.info("Vínculos foram processados para o pátio {} e veículos {}", impoundLotId, vehicleIds);

        } catch (NotFoundException e) {
            log.error("Erro de validação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar vínculos para o pátio {}: {}", impoundLotId, e.getMessage(), e);
            throw new RuntimeException("Falha ao criar vínculos", e);
        }
    }
    
    public void triggerMultipleTowTruckVehicle(String towTruckId, Set<String> vehicleIds) {
        log.info("Iniciando criação de vínculos para o guincho {} com veículos {}", towTruckId, vehicleIds);

        try {
            // Verificar se o guincho existe
            companyRepository.findById(towTruckId).orElseThrow(() -> new NotFoundException("Guincho não encontrado: " + towTruckId));
            log.info("Guincho {} verificado com sucesso", towTruckId);

            // Iterar sobre cada vehicleId para validação e criação do vínculo
            for (String vehicleId : vehicleIds) {
                log.info("Processando veículo id: {}", vehicleId);

                try {
                    // Verificar se o veículo existe
                    vehicleRepository.findById(vehicleId).orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + vehicleId));
                    log.info("Veículo {} verificado com sucesso", vehicleId);

                    // Criar novo vínculo entre veículo e guincho
                    VehicleCompanyCollection newLink = VehicleCompanyCollection.builder()
                            .vehicleId(vehicleId)
                            .companyId(towTruckId)
                            .createdAt(LocalDateTime.now())
                            .dateTrigger(LocalDateTime.now())
                            .build();

                    vehicleCompanyRepository.save(newLink);
                    log.info("Vínculo criado com sucesso entre TowTruckID={} e VeículoID={}", towTruckId, vehicleId);

                } catch (NotFoundException e) {
                    log.error("Erro de validação para veículo {}: {}", vehicleId, e.getMessage());
                    continue;
                } catch (DuplicateKeyException dkEx) {
                    log.warn("Vínculo já existe entre TowTruckID={} e VeículoID={}", towTruckId, vehicleId);
                    continue;
                } catch (Exception e) {
                    log.error("Erro inesperado ao criar vínculo para veículo {}: {}", vehicleId, e.getMessage(), e);
                    continue;
                }
            }

            log.info("Vínculos foram processados para o guincho {} e veículos {}", towTruckId, vehicleIds);

        } catch (NotFoundException e) {
            log.error("Erro de validação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar vínculos para o guincho {}: {}", towTruckId, e.getMessage(), e);
            throw new RuntimeException("Falha ao criar vínculos", e);
        }
    }

    public void triggerMultipleLocatorVehicle(String locatorId, Set<String> vehicleIds) {
        log.info("Iniciando criação de vínculos para o localizador {} com veículos {}", locatorId, vehicleIds);

        try {
            // Verificar se o localizador existe
            companyRepository.findById(locatorId).orElseThrow(() -> new NotFoundException("Localizador não encontrado: " + locatorId));
            log.info("Localizador {} verificado com sucesso", locatorId);

            // Iterar sobre cada vehicleId para validação e criação do vínculo
            for (String vehicleId : vehicleIds) {
                log.info("Processando veículo id: {}", vehicleId);

                try {
                    // Verificar se o veículo existe
                    vehicleRepository.findById(vehicleId).orElseThrow(() -> new NotFoundException("Veículo não encontrado: " + vehicleId));
                    log.info("Veículo {} verificado com sucesso", vehicleId);

                    // Criar novo vínculo entre veículo e localizador
                    VehicleCompanyCollection newLink = VehicleCompanyCollection.builder()
                            .vehicleId(vehicleId)
                            .companyId(locatorId)
                            .createdAt(LocalDateTime.now())
                            .dateTrigger(LocalDateTime.now())
                            .build();

                    vehicleCompanyRepository.save(newLink);
                    log.info("Vínculo criado com sucesso entre LocatorID={} e VeículoID={}", locatorId, vehicleId);

                } catch (NotFoundException e) {
                    log.error("Erro de validação para veículo {}: {}", vehicleId, e.getMessage());
                    continue;
                } catch (DuplicateKeyException dkEx) {
                    log.warn("Vínculo já existe entre LocatorID={} e VeículoID={}", locatorId, vehicleId);
                    continue;
                } catch (Exception e) {
                    log.error("Erro inesperado ao criar vínculo para veículo {}: {}", vehicleId, e.getMessage(), e);
                    continue;
                }
            }

            log.info("Vínculos foram processados para o localizador {} e veículos {}", locatorId, vehicleIds);

        } catch (NotFoundException e) {
            log.error("Erro de validação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar vínculos para o localizador {}: {}", locatorId, e.getMessage(), e);
            throw new RuntimeException("Falha ao criar vínculos", e);
        }
    }
    
    public List<CompanyCollection> findCompaniesByVehicleLicensePlate(String licensePlate) {
        log.info("Buscando veículo pela placa: {}", licensePlate);

        // Busca o veículo pela placa
        VehicleCollection vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado para a placa: " + licensePlate));

        log.info("Veículo encontrado com ID: {}", vehicle.getId());

        // Continua a lógica para buscar as empresas associadas ao vehicleId encontrado
        List<VehicleCompanyCollection> vehicleCompanies = vehicleCompanyRepository.findByVehicleId(vehicle.getId())
                .orElseThrow(() -> new NotFoundException("Não foram encontradas empresas para o veículo"));

        // Mapeia os companyId e busca as empresas relacionadas
        return vehicleCompanies.stream()
            .map(vehicleCompany -> {
                // Busca os dados da empresa associada ao companyId
                return companyRepository.findById(vehicleCompany.getCompanyId())
                    .orElseThrow(() -> new NotFoundException("Empresa não encontrada para ID: " + vehicleCompany.getCompanyId()));
            })
            .collect(Collectors.toList());
    }
}
