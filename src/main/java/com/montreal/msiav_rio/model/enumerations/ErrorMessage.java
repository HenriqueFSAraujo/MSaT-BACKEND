package com.montreal.msiav_rio.model.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    INTERNAL_ERROR(0, "Erro interno na aplicação"),
    INVALID_USERNAME_PASSWORD(1, "Usuário/senha inválido."),
    INVALID_TOKEN(2, "Token inválido."),
    USER_NOT_AUTHORIZED(3, "Este tipo de usuário não está autorizado a realizar esta funcionalidade."),
    USER_NO_PROFILE(4, "Este usuário não tem perfil para realizar esta operação."),
    INVALID_CONTRACT_NUMBER(5, "Número do contrato inválido. Verifique o tamanho limite."),
    INVALID_DOCUMENT(6, "CPF/CNPJ inválido."),
    INVALID_LICENSE_PLATE_STATE(7, "UF da Placa inválida. Verifique o tamanho limite."),
    INVALID_PROTOCOL_NUMBER(8, "Número do protocolo inválido. Verifique o tamanho limite."),
    INVALID_LICENSE_PLATE(9, "Placa inválida. Verifique o tamanho limite. Verifique os layouts válidos AAANNNN ou AAANANN."),
    UNEXPECTED_ERROR(9999, "Erro inesperado: <mensagem original do software>");

    private final int code;
    private final String message;

    public static ErrorMessage byCode(int code) {
        for (ErrorMessage error : values()) {
            if (error.code == code) {
                return error;
            }
        }
        return UNEXPECTED_ERROR;
    }

}
