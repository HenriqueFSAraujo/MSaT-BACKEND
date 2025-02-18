package com.montreal.msiav_bh.collection;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.montreal.msiav_bh.enumerations.VehicleStageEnum;
import com.montreal.msiav_bh.enumerations.VehicleStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "vehicle")
@CompoundIndex(name = "licensePlate_contractNumber_idx", def = "{'licensePlate': 1, 'contractNumber': 1}", unique = true)
public class VehicleCollection {
    @Id
    private String id; // Identificador único do veículo

    @NotBlank(message = "A placa do veículo é obrigatória.")
    private String licensePlate; // Placa do veículo

    @NotBlank(message = "O modelo do veículo é obrigatório.")
    private String model; // Modelo do veículo

    @NotBlank(message = "O estado de registro do veículo é obrigatório.")
    @Size(min = 2, max = 2, message = "O estado de registro deve conter exatamente 2 caracteres (ex: SP).")
    private String registrationState; // Estado de registro do veículo

    @NotBlank(message = "O nome do credor é obrigatório.")
    private String creditorName; // Nome do credor relacionado ao veículo

    @NotBlank(message = "O número do contrato é obrigatório.")
    @Pattern(regexp = "\\d+", message = "O número do contrato deve conter apenas dígitos.")
    private String contractNumber; // Número do contrato associado ao veículo

    @NotNull(message = "A fase atual do veículo é obrigatória.")
    private VehicleStageEnum stage; // Fase atual do veículo no processo

    @NotNull(message = "O status do veículo é obrigatório.")
    private VehicleStatusEnum status; // Status atual do veículo

    @PastOrPresent(message = "A data da solicitação não pode estar no futuro.")
    private LocalDate requestDate; // Data da solicitação relacionada ao veículo

    @PastOrPresent(message = "A data de apreensão do veículo não pode estar no futuro.")
    private LocalDateTime vehicleSeizureDateTime; // Data e hora da apreensão do veículo

    @PastOrPresent(message = "A data da última movimentação não pode estar no futuro.")
    private LocalDateTime lastMovementDate; // Data e hora da última movimentação do veículo

    @FutureOrPresent(message = "A data de apreensão agendada deve estar no futuro ou presente.")
    private LocalDateTime seizureScheduledDate; // Data e hora agendada para apreensão do veículo

}
