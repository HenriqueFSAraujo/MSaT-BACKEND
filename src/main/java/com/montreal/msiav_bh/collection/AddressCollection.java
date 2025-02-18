package com.montreal.msiav_bh.collection;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "address")
@CompoundIndex(name = "unique_address", def = "{'postalCode': 1, 'street': 1, 'number': 1, 'neighborhood': 1, 'city': 1}", unique = true)
public class AddressCollection {

    @Id
    private String id;

    @NotBlank(message = "O código postal (CEP) é obrigatório.")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato 99999-999.")
    private String postalCode; // Código postal (CEP) do endereço

    @NotBlank(message = "O nome da rua é obrigatório.")
    private String street; // Nome da rua do endereço

    @NotBlank(message = "O número do imóvel é obrigatório.")
    @Pattern(regexp = "\\d+", message = "O número do imóvel deve conter apenas dígitos.")
    private String number; // Número do imóvel no endereço

    @NotBlank(message = "O bairro é obrigatório.")
    private String neighborhood; // Bairro onde o endereço está localizado

    private String complement; // Complemento do endereço (ex.: apartamento, bloco)

    @NotBlank(message = "O estado é obrigatório.")
    private String state; // Estado onde o endereço está localizado

    @NotBlank(message = "A cidade é obrigatória.")
    private String city; // Cidade onde o endereço está localizado

    private String note; 	// Nota ou observação adicional sobre o endereço

}
