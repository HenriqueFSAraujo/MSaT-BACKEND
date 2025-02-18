package com.montreal.msiav_bh.collection;


import com.montreal.msiav_bh.enumerations.VehicleConditionEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "vehicle_seizure")
public class VehicleSeizureCollection {
    @Id
    private String id; // Identificador único

    @NotBlank(message = "O campo userId é obrigatório.")
    private Integer userId; // Identificador do usuário associado

    @NotBlank(message = "O campo vehicleId é obrigatório.")
    private String vehicleId; // Identificador do veículo

    @NotBlank(message = "O campo addressId é obrigatório.")
    private String addressId; // Identificador do endereço associado

    @NotBlank(message = "O campo companyId é obrigatório.")
    private String companyId; // Identificador da empresa

    @NotBlank(message = "O campo vehicleCondition é obrigatório.")
    private VehicleConditionEnum vehicleCondition;

    @NotNull(message = "O campo seizureDate é obrigatório.")
    private LocalDateTime seizureDate; // Data da apreensão

    @CreatedDate
    @NotNull(message = "O campo createdAt é obrigatório.")
    private LocalDateTime createdAt; // Data de criação do registro

    @NotBlank(message = "O campo description é obrigatório.")
    private String description; // Descrição opcional

}
