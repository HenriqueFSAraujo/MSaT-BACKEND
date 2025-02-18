package com.montreal.msiav_bh.collection;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.enumerations.PhoneTypeEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Document(collection = "company")
public class CompanyCollection {

    @Id
    private String id; // Identificador único

    @NotBlank(message = "O nome da empresa é obrigatório.")
    private String name; // Nome da empresa

    @NotBlank(message = "Endereço de email da empresa é obrigatório")
    private String email; // Endereço de email da empresa

    @NotBlank(message = "Documento da empresa é obrigatório")
    private String document; // Documento da empresa (CNPJ ou equivalente)

    @NotBlank(message = "DDD do telefone é obrigatório")
    @Pattern(regexp = "\\d{2}", message = "O DDD deve conter dois dígitos.")
    private String phoneDDD; // DDD do telefone da empresa

    @NotBlank(message = "Número de telefone é obrigatório")
    private String phoneNumber; // Número de telefone da empresa

    @NotNull(message = "Tipo de telefone é obrigatório")
    private PhoneTypeEnum phoneType; // Tipo de telefone da empresa

    @NotNull(message = "Endereço da empresa é obrigatório")
    @Valid
    @Field("address")
    private AddressCollection address; // Endereço físico da empresa

    @NotBlank(message = "Nome do responsável pela empresa é obrigatório")
    private String nameResponsible; // Nome do responsável pela empresa

    @NotBlank(message = "Tipo da empresa é obrigatório")
    private CompanyTypeEnum companyType; // Tipo de empresa

    @NotBlank(message = "estado de atividade da empresa é obrigatorio")
    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

}
