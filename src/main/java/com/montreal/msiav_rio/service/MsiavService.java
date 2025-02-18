package com.montreal.msiav_rio.service;

import com.montreal.core.domain.exception.ClientServiceException;
import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.exception_handler.ProblemType;
import com.montreal.msiav_rio.client.MsiavClient;
import com.montreal.msiav_rio.model.request.NotificacaoContratoRequest;
import com.montreal.msiav_bh.service.SystemParameterService;
import com.montreal.msiav_rio.model.response.ContractCompleteResponse;
import com.montreal.msiav_rio.model.response.NotificacaoContratoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MsiavService {

    public static final String SYSTEM_NAME = "MSIAV";

    private final MsiavClient msiavClient;
    private final SystemParameterService systemParameterService;

    public NotificacaoContratoResponse getContract(LocalDate startDate, LocalDate endDate) {

        try {

            var token = generateToken();

            return msiavClient.getContract(token, startDate, endDate);

        } catch (ClientServiceException e) {
            throw e;

        } catch (Exception e) {
            throw new NegocioException(ProblemType.ERRO_NEGOCIO, "Erro ao enviar contrato para o MSIAV", e);
        }

    }


    public ContractCompleteResponse getDataContractComplete(NotificacaoContratoRequest request) {

        try {

            var token = generateToken();

            return msiavClient.getDataContractComplete(request, token);

        } catch (ClientServiceException e) {
            throw e;

        } catch (Exception e) {
            throw new NegocioException(ProblemType.ERRO_NEGOCIO, "Erro ao enviar contrato para o MSIAV", e);
        }

    }


    private String generateToken() {

        var userApi = systemParameterService.getSystemParameter(SYSTEM_NAME, "USER");
        var passApi = systemParameterService.getSystemParameter(SYSTEM_NAME, "PASSWORD");

        return msiavClient.getToken(userApi.getValue(), passApi.getValue());

    }

}
