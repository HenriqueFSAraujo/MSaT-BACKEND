package com.montreal.msiav_bh.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.montreal.msiav_bh.collection.SeizureDateCollection;
import com.montreal.msiav_bh.collection.VehicleCollection;
import com.montreal.msiav_bh.dto.request.SeizureDateRequest;
import com.montreal.msiav_bh.repository.SeizureDateRepository;
import com.montreal.msiav_bh.repository.VehicleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeizureDateService {

    private final SeizureDateRepository seizureDateRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public SeizureDateCollection update(SeizureDateCollection request) {
        try {
            log.info("Iniciando atualização da data de apreensão para o ID: {}", request.getId());

            // Recupera o SeizureDate pelo ID
            SeizureDateCollection existingSeizureDate = seizureDateRepository.findById(request.getId())
                    .orElseThrow(() -> {
                        log.error("SeizureDate não encontrado com ID: {}", request.getId());
                        return new RuntimeException("SeizureDate não encontrado com ID: " + request.getId());
                    });

            // Atualiza os campos com os dados recebidos
            existingSeizureDate.setSeizureDate(request.getSeizureDate());

            // Atualiza o campo createdAt para a data e hora atuais se estiver nulo
            if (existingSeizureDate.getCreatedAt() == null) {
                existingSeizureDate.setCreatedAt(LocalDateTime.now());
            }

            // Salva o registro atualizado no banco de dados
            SeizureDateCollection savedSeizureDate = seizureDateRepository.save(existingSeizureDate);
            log.info("Atualização bem-sucedida para o SeizureDate ID: {}", request.getId());

            return savedSeizureDate;

        } catch (Exception e) {
            log.error("Erro ao atualizar o SeizureDate com ID: {}", request.getId(), e);
            throw new RuntimeException("Erro ao atualizar o SeizureDate com ID: " + request.getId(), e);
        }
    }

    public SeizureDateCollection findById(String id) {
        return seizureDateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SeizureDate não encontrado com ID: " + id));
    }

    public List<SeizureDateCollection> findByVehicleId(String veiculeId) {
        VehicleCollection veiculo = vehicleRepository.findById(veiculeId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + veiculeId));
        List<SeizureDateCollection> seizureDates = seizureDateRepository.findByVeiculeId(veiculo.getId());
        log.info("Encontrado {} registros de apreensão para o veículo com ID: {}", seizureDates.size(), veiculeId);
        return seizureDates;
    }

    @Transactional
    public SeizureDateCollection addOrUpdateSeizureDateToVehicle(SeizureDateRequest request) {
    	String vehicleId = request.getVehicleId();
        VehicleCollection vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + vehicleId));


        Optional<SeizureDateCollection> existingSeizureDateOpt = seizureDateRepository.findByVeiculeId(vehicleId)
                .stream().findFirst();

        if (existingSeizureDateOpt.isPresent()) {
            SeizureDateCollection existingSeizureDate = existingSeizureDateOpt.get();
            existingSeizureDate.setSeizureDate(request.getSeizureDate());
            existingSeizureDate.setCreatedAt(LocalDateTime.now());


            vehicle.setVehicleSeizureDateTime(request.getSeizureDate());
           // vehicle.setLastMovementDate(request.getSeizureDate());
            vehicleRepository.save(vehicle);

            log.info("Registro de apreensão atualizado para o veículo com ID: {}", vehicleId);
            return seizureDateRepository.save(existingSeizureDate); // Atualiza o registro existente

        } else {
        	SeizureDateCollection seizureDate = new SeizureDateCollection();
        	
        	seizureDate.setVeiculeId(vehicleId);
            //vehicle.setVehicleSeizureDateTime(seizureDate);
        	seizureDate.setSeizureDate(request.getSeizureDate());
            seizureDate.setCreatedAt(LocalDateTime.now());

            SeizureDateCollection newSeizureDate = seizureDateRepository.save(seizureDate);

            // Atualiza a data de apreensão no veículo
            vehicle.setVehicleSeizureDateTime(request.getSeizureDate());
            vehicleRepository.save(vehicle);

            log.info("Novo registro de apreensão criado para o veículo com ID: {}", vehicleId);
            return newSeizureDate; // Retorna o novo registro
        }
    }

}
