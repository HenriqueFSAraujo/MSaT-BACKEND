package com.montreal.msiav_bh.collection;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "user_vehicle_association")
public class UserVehicleAssociationCollection {

    @Id
    private String id; // Identificador único da associação

    @NotBlank(message = "O ID do usuário é obrigatório.")
    private Long userId; // ID do usuário

    @NotBlank(message = "O ID do veículo é obrigatório.")
    private String vehicleId; // ID do veículo

    @NotBlank(message = "O usuário responsável pelo vínculo é obrigatório.")
    private Long associatedBy; // Usuário que realizou o vínculo

    @NotNull(message = "A data de criação é obrigatória.")
    private LocalDateTime createdAt; // Data e hora do vínculo
}
