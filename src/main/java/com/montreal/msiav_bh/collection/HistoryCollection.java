package com.montreal.msiav_bh.collection;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.montreal.msiav_bh.collection.data.Collected;
import com.montreal.msiav_bh.collection.data.ImpoundLot;
import com.montreal.msiav_bh.collection.data.Location;
import com.montreal.msiav_bh.enumerations.TypeHistoryEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "history")
public class HistoryCollection {

    @Id
    private String id; // Identificador único do histórico

    @NotBlank(message = "O ID do veículo é obrigatório.")
    private String idVehicle; // Identificador do veículo associado ao histórico

    @NotBlank(message = "A placa do veículo é obrigatória.")
    private String licensePlate; // Placa do veículo

    @NotBlank(message = "O modelo do veículo é obrigatório.")
    private String model; // Modelo do veículo

    @NotBlank(message = "O nome do credor é obrigatório.")
    private String creditorName; // Nome do credor relacionado ao veículo

    @NotBlank(message = "O número do contrato é obrigatório.")
    private String contractNumber; // Número do contrato associado ao veículo

    @NotNull(message = "A data e hora de criação do histórico são obrigatórias.")
    private LocalDateTime creationDateTime; // Data e hora de criação do histórico

    @NotNull(message = "O tipo do histórico é obrigatório.")
    private TypeHistoryEnum typeHistory; // Tipo do histórico, representado por um enum

    @NotNull(message = " Localização do veículo no momento do histórico é obrigatório.")
    private Location location; // Localização do veículo no momento do histórico

    @NotNull(message = "// Informações sobre a coleta do veículo é obrigatório.")
    private Collected collected; // Informações sobre a coleta do veículo

    @NotNull(message = "Informações sobre o pátio de apreensão do veículo é obrigatório.")
    private ImpoundLot impoundLot; // Informações sobre o pátio de apreensão do veículo
}

