package com.montreal.msiav_bh.service;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.collection.UserVehicleAssociationCollection;
import com.montreal.msiav_bh.collection.VehicleCollection;
import com.montreal.msiav_bh.dto.request.VehicleFilterRequest;
import com.montreal.msiav_bh.dto.request.VehicleRequest;
import com.montreal.msiav_bh.dto.response.PageVehicleResponse;
import com.montreal.msiav_bh.dto.response.VehicleResponse;
import com.montreal.msiav_bh.enumerations.VehicleStageEnum;
import com.montreal.msiav_bh.enumerations.VehicleStatusEnum;
import com.montreal.msiav_bh.mapper.IVehicleMapper;
import com.montreal.msiav_bh.repository.VehicleRepository;
import com.montreal.msiav_rio.model.request.NotificacaoContratoRequest;
import com.montreal.msiav_rio.model.response.ContractCompleteResponse;
import com.montreal.msiav_rio.model.response.NotificacaoContratoResponse;
import com.montreal.msiav_rio.service.MsiavService;
import com.montreal.oauth.domain.dto.response.UserResponse;
import com.montreal.oauth.domain.enumerations.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final MsiavService msiavService;
    private final VehicleRepository vehicleRepository;
    private final MongoTemplate mongoTemplate;
    private final UserMongoService userMongoService;

    @Transactional
    public PageVehicleResponse listAllAndApiRio(Pageable page, VehicleFilterRequest filter) {

        getVehiclesApiRio(filter);

        Query query = new Query().with(page);

        // Verifica se o usuário é um agente oficial
        boolean isAgenteOficial = checkUserIsAgenteOficial();

        if (isAgenteOficial) {
            // Recupera o ID do usuário autenticado
            UserResponse userResponse = userMongoService.getAuthenticatedUser();
            Long userId = userResponse.getId();

            // Busca associações de veículos para o agente oficial
            List<UserVehicleAssociationCollection> userVehicleAssociations = mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId)),
                UserVehicleAssociationCollection.class
            );

            // Coleta os IDs dos veículos associados
            List<String> vehicleIds = userVehicleAssociations.stream()
                .map(UserVehicleAssociationCollection::getVehicleId)
                .toList();

            // Caso não haja veículos associados, retorna uma página vazia
            if (vehicleIds.isEmpty()) {
                // Cria uma página vazia de VehicleCollection
                Page<VehicleCollection> emptyPage = new PageImpl<>(new ArrayList<>(), page, 0L);

                // Mapeia para PageVehicleResponse usando o mapper existente
                return IVehicleMapper.INSTANCE.toCollectionVehicleResponse(emptyPage);
            }

            // Adiciona filtro pelos IDs dos veículos associados
            query.addCriteria(Criteria.where("id").in(vehicleIds));

            // Opcionalmente, filtra por data se fornecido no filtro
            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                log.info("Aplicando filtro de data para agente oficial: {} até {}", filter.getStartDate(), filter.getEndDate());
                query.addCriteria(Criteria.where("requestDate").gte(filter.getStartDate()).lte(filter.getEndDate()));
            } else {
                log.info("Buscando veículos associados ao agente oficial sem filtro de data");
            }
        } else {
            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                log.info("Aplicando filtro de data diretamente no banco: {} até {}", filter.getStartDate(), filter.getEndDate());
                query.addCriteria(Criteria.where("requestDate").gte(filter.getStartDate()).lte(filter.getEndDate()));
            } else {
                log.info("Buscando todos os veículos sem filtro de data");
            }
        }

        // Executa a consulta no MongoDB
        List<VehicleCollection> vehicles = mongoTemplate.find(query, VehicleCollection.class);
        long total = mongoTemplate.count(query.skip(-1).limit(-1), VehicleCollection.class);

        // Retorna os resultados paginados
        Page<VehicleCollection> pageVehicle = new PageImpl<>(vehicles, page, total);
        return IVehicleMapper.INSTANCE.toCollectionVehicleResponse(pageVehicle);
    }
    
    public PageVehicleResponse findAll(Pageable page) {
        log.info("Buscando veiculos na base local");
        var pageVehicle = vehicleRepository.findAll(page);
        return IVehicleMapper.INSTANCE.toCollectionVehicleResponse(pageVehicle);
    }

    public ContractCompleteResponse findContractByNumberContract(String numberContract) {
        var request = NotificacaoContratoRequest.builder().numeroContrato(numberContract).build();
        return msiavService.getDataContractComplete(request);
    }

    public VehicleResponse alterStageAndStatus(VehicleStageEnum stage, VehicleStatusEnum status, String vehicleId) {

        var vehicle = findById(vehicleId);

        vehicle.setStage(stage);
        vehicle.setStatus(status);
        vehicle.setLastMovementDate(LocalDateTime.now());

        vehicleRepository.save(vehicle);
        return IVehicleMapper.INSTANCE.toVehicleResponse(vehicle);
    }

    public VehicleResponse updateDateSeizureByVehicleId(String vehicleId) {

        var vehicle = findById(vehicleId);

        vehicle.setVehicleSeizureDateTime(LocalDateTime.now());
        vehicleRepository.save(vehicle);

        return IVehicleMapper.INSTANCE.toVehicleResponse(vehicle);
    }

    public VehicleCollection findById(String vehicleId) {
        log.info("Buscando veiculo na base local com id: {}", vehicleId);
        return vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Veiculo não encontrado"));
    }

    private void getVehiclesApiRio(VehicleFilterRequest filter) {
        log.info("Buscando veículos no MSIAV com filtro: {}", filter);
        try {
            var notificacaoContratoResponse = msiavService.getContract(filter.getStartDate(), filter.getEndDate());

            if (!isEmpty(notificacaoContratoResponse.getData())) {

                var vehicles = getVehicles(notificacaoContratoResponse);
                log.info("{} Veículos recuperados com sucesso", vehicles.size());

                vehicles.forEach(vehicle -> {
                    var vehicleOptional = vehicleRepository.findByLicensePlateAndContractNumber(vehicle.getLicensePlate(), vehicle.getContractNumber());
                    if (vehicleOptional.isEmpty()) {
                        log.info("Salvando veículo na base local: Contrato: {} - Placa: {}", vehicle.getContractNumber(), vehicle.getLicensePlate());

                        NotificacaoContratoRequest request = new NotificacaoContratoRequest();
                        byte[] dadosPdf = Base64.getDecoder().decode(msiavService.getDataContractComplete(request).getData().getContrato().getCertidaoBuscaApreensao());
                        vehicleRepository.save(IVehicleMapper.INSTANCE.toVehicle(vehicle));
                    }
                });
            }
            } catch (Exception e) {
            log.error("Erro ao buscar veículos no MSIAV", e);
        }
    }

    public VehicleCollection findByContractNumber(String contractNumber) {
        log.info("Buscando veiculo na base local com contrato: {}", contractNumber);
        return vehicleRepository.findByContractNumber(contractNumber)
                .orElseThrow(() -> new RuntimeException("Veiculo não encontrado"));
    }

    private static List<VehicleRequest> getVehicles(NotificacaoContratoResponse notificacaoContratoResponse) {
        List<VehicleRequest> vehicles = new ArrayList<>();
        notificacaoContratoResponse.getData().forEach(dataNotificacaoResponse -> {
            var numberContract = dataNotificacaoResponse.getNumeroContrato();

            var veiculoList = dataNotificacaoResponse.getVeiculos()
                    .stream()
                    .map(vehicle -> IVehicleMapper.INSTANCE.toInitialVehicleRequest(dataNotificacaoResponse, vehicle, numberContract)
            ).toList();

            vehicles.addAll(veiculoList);
        });
        return vehicles;
    }
    
    public VehicleResponse findVehicleById(String vehicleId) {
        log.info("Buscando veiculo na base local com ID: {}", vehicleId);
        // Busca o veículo pelo ID
        var vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new RuntimeException("Veiculo com ID " + vehicleId + " não foi encontrado"));
        // Mapeia o entity Vehicle para o DTO VehicleResponse
        return IVehicleMapper.INSTANCE.toVehicleResponse(vehicle);
    }
    
    public void validateVehicleIdExists(String vehicleId) {
        log.info("Validando se a veiculo com ID {} existe.", vehicleId);
        if (!vehicleRepository.existsById(vehicleId)) {
            log.error("Veiculo com ID {} não encontrado.", vehicleId);
            throw new NotFoundException(String.format("Veículo %s não encontrado.", vehicleId));
        }
    }
    
    private boolean checkUserIsAgenteOficial() {
    	var userInfo = userMongoService.getLoggedInUser();

        // Verifica se o usuário possui a role necessária
        boolean hasPermission = userInfo.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleEnum.ROLE_AGENTE_OFICIAL);

        if (hasPermission) {
            return true;
        }
        
        return false;
    }

}
