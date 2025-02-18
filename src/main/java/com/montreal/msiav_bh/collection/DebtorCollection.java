package com.montreal.msiav_bh.collection;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Document(collection = "debtor")
public class DebtorCollection {

    @Id
    private String id;

    @NotNull(message = "Nome do devedor não pode ser nulo")
    @Size(min = 1, max = 100, message = "Nome do devedor deve ter entre 1 e 100 caracteres")
    private String name;

    @NotNull(message = "CPF/CNPJ do devedor não pode ser nulo")
    private String cpfCnpj;



}
