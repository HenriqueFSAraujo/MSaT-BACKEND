package com.montreal.msiav_bh.collection;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "vehicle_address")
@CompoundIndex(name = "unique_vehicle_address", def = "{'vehicleId': 1, 'addressId': 1}", unique = true)
public class VehicleAddressCollection {
    @Id
    private String id; // Identificador único do documento

    @NotBlank(message = "O ID do veículo não pode estar vazio.")
    private String vehicleId; // Identificador único do veículo

    @NotBlank(message = "O ID do endereço não pode estar vazio.")
    private String addressId; // Identificador único do endereço

    @NotNull(message = "A data de associação é obrigatória.")
    private LocalDateTime associatedDate; // Data em que o endereço foi associado ao veícul
}
