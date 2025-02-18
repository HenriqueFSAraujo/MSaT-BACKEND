package com.montreal.oauth.domain.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.br.CPF;

import java.sql.Timestamp;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @CPF
    @NotBlank
    private String cpf;

    private String companyId;

    private String password;
    private boolean enabled;
    private String link;
    private boolean isReset;
    private Timestamp resetAt;
    private Set<UserRoleRequest> roles;
    private boolean createdByAdmin;
    private boolean passwordChangedByUser;

}