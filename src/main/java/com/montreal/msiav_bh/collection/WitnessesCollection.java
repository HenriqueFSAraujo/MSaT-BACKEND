package com.montreal.msiav_bh.collection;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Getter
@Setter
@Document(collection = "witnesses")

public class WitnessesCollection {

    @Id
    private String id;

    @NotBlank(message = "O nome da testemunha não pode ser nulo ou vazio.")
    private String name;

    @NotBlank(message = "O RG da testemunha não pode ser nulo ou vazio.")
    @Pattern(regexp = "\\d{7,12}", message = "O RG deve conter entre 7 e 12 dígitos numéricos.")
    private String rg;

    @NotBlank(message = "O CPF da testemunha não pode ser nulo ou vazio.")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "O CPF deve seguir o formato 000.000.000-00.")
    private String cpf;

}
