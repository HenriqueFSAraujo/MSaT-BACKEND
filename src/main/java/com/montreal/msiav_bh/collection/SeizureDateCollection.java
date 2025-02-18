package com.montreal.msiav_bh.collection;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "seizure_date")
@CompoundIndex(name = "unique_vehicle_seizure_date", def = "{'vehicleId': 1, 'seizureDate': 1}", unique = true)
public class SeizureDateCollection {


    @Id
    private String id;  // Identificador único do documento

    @NotBlank(message = "O ID do veículo não pode estar vazio.")
    private String veiculeId; // ID do veículo associado

    @NotNull(message = "A data de apreensão é obrigatória.")
    private LocalDateTime seizureDate; // Data e hora da apreensão
    
    @CreatedDate
    private LocalDateTime createdAt; 	// Data de criação do documento

}
