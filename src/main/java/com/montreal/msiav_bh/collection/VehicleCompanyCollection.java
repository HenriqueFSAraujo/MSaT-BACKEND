package com.montreal.msiav_bh.collection;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@Document(collection = "vehicle_company")
@CompoundIndex(name = "unique_vehicle_company", def = "{'vehicleId': 1, 'companyId': 1}", unique = true)
public class VehicleCompanyCollection {

    @Id
    private String id; 					// Identificador único da associação veículo-empresa

    @NotBlank(message = "O ID do veículo é obrigatório.")
    private String vehicleId; 			// Identificador único do veículo

    @NotBlank(message = "O ID da emepresa é obrigatório.")
    private String companyId; 			// Identificador único da empresa

    @CreatedDate
    private LocalDateTime createdAt; 	// Data e hora em que a associação foi criada
    
    private LocalDateTime dateTrigger; 	// Data e hora em que ocorreu um evento de acionamento relacionado

}