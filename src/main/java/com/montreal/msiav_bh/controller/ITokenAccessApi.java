package com.montreal.msiav_bh.controller;

import com.montreal.oauth.domain.enumerations.TwoFactorTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Access Token")
public interface ITokenAccessApi {

    @PostMapping("/send-token/{idUser}/{type}")
    @Operation(summary = "Enviar token por SMS ou e-mail",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token enviado com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Acesso não autorizado"
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    void sendToken(@PathVariable Long idUser, @PathVariable TwoFactorTypeEnum type);

    @Operation(summary = "Validar token do usuário")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/validate-token/{idUser}/{token}")
    void validateToken(@PathVariable String token, @PathVariable Long idUser);

}
