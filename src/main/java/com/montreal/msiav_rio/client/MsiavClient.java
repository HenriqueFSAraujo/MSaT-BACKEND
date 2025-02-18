package com.montreal.msiav_rio.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.montreal.core.client.BaseClient;
import com.montreal.core.domain.exception.InternalErrorException;
import com.montreal.msiav_rio.model.request.AuthRequest;
import com.montreal.msiav_rio.model.request.NotificacaoContratoRequest;
import com.montreal.msiav_rio.model.response.AuthResponse;
import com.montreal.msiav_rio.model.response.ContractCompleteResponse;
import com.montreal.msiav_rio.model.response.NotificacaoContratoResponse;
import com.montreal.msiav_rio.properties.MSiavProperties;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;


@Service
public class MsiavClient extends BaseClient {

    private static final String AUTHENTICATE_URL = "%s/sanctum/token";
    private static final String RECEPTION_ASSET_URL = "%s/recepcaoContrato/receber";
    private static final String RECEPTION_CONSULT_URL = "%s/recepcaoContrato/periodo/%s/%s";
    private static final String RECEPTION_CONSULT_CANCEL_URL = "%s/recepcaoContrato/cancelados/periodo/%s/%s";

    private final MSiavProperties msiavProperties;

    public MsiavClient(ObjectMapper mapper, MSiavProperties msiavProperties) {

        super(mapper);
        this.msiavProperties = msiavProperties;
    }

    public ContractCompleteResponse getDataContractComplete(NotificacaoContratoRequest request, String token) {

        try {
            var url = String.format(RECEPTION_ASSET_URL, msiavProperties.getUrl());
            return executePostRequest(url, request, token, new TypeReference<ContractCompleteResponse>(){});
        } catch (Exception e) {
            handleException(e, RECEPTION_ASSET_URL);
            return null;
        }

    }

    public NotificacaoContratoResponse getContract(String token, LocalDate startDate, LocalDate endDate) {

        try {
            var url = String.format(RECEPTION_CONSULT_URL, msiavProperties.getUrl(), startDate, endDate);
            return executeGetRequest(url, token, new TypeReference<NotificacaoContratoResponse>(){});
        } catch (Exception e) {
            handleException(e, RECEPTION_CONSULT_URL);
            return null;
        }

    }

    public NotificacaoContratoResponse getContractCancel(String token, LocalDate startDate, LocalDate endDate) {

        try {
            var url = String.format(RECEPTION_CONSULT_CANCEL_URL, msiavProperties.getUrl(), startDate, endDate);
            return executeGetRequest(url, token, new TypeReference<NotificacaoContratoResponse>(){});
        } catch (Exception e) {
            handleException(e, RECEPTION_CONSULT_CANCEL_URL);
            return null;
        }

    }

    public String getToken(String user, String pass) {

        AuthResponse authResponse = authenticate(AuthRequest.builder().username(user).password(pass).build());

        return Optional.ofNullable(authResponse)
                .map(it -> it.getData().getToken())
                .orElseThrow(() -> new InternalErrorException("Não foi possível obter o token de autenticação"));

    }

    private AuthResponse authenticate(AuthRequest request) {
        try {
            var url = String.format(AUTHENTICATE_URL, msiavProperties.getUrl());
            return executePostRequest(url, request, null, new TypeReference<AuthResponse>(){});
        } catch (Exception e) {
            handleException(e, AUTHENTICATE_URL);
            return null;
        }
    }

}
