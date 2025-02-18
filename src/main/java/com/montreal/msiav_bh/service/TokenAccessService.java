package com.montreal.msiav_bh.service;

import com.montreal.broker.components.SgdBrokerComponent;
import com.montreal.broker.dto.request.DigitalSendRequest;
import com.montreal.broker.service.SgdBrokerService;
import com.montreal.core.domain.component.EmailComponent;
import com.montreal.core.domain.component.SmsComponent;
import com.montreal.core.domain.exception.*;
import com.montreal.core.domain.helper.GenerationTokenHelper;
import com.montreal.oauth.domain.enumerations.TwoFactorTypeEnum;
import com.montreal.oauth.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAccessService {

    public static final String SUBJECT_TOKEN = "Token Acessar sistema HubRecupera";
    private final UserService userService;
    private final SgdBrokerService sgdBrokerService;
    private final SgdBrokerComponent sgdBrokerComponent;

    public void sendToken(Long idUser, TwoFactorTypeEnum type) {

        log.info("Enviando token por {} para o usuário código {}", type, idUser);

        try {

            var user = userService.getUserById(idUser);

            log.info("Usuário {} encontrado", user.getUsername());

            var token = GenerationTokenHelper.generateToken();

            log.info("Gravando token na base para o usuário {}", user.getUsername());
            user.setTokenTemporary(token);
            user.setTokenExpiredAt(GenerationTokenHelper.generateTokenExpiredAt());
            userService.update(user);

            var digitalSendRequest = DigitalSendRequest.builder().build();
            if (TwoFactorTypeEnum.SMS.equals(type)) {
                var template = SmsComponent.getTemplateSendToken(user.getUsername(), token);
                digitalSendRequest = sgdBrokerComponent.createTypeSms(template, user.getPhone());

            } else if (TwoFactorTypeEnum.EMAIL.equals(type)) {
                var template = EmailComponent.getTemplateSendToken(user.getUsername(), token);
                digitalSendRequest = sgdBrokerComponent.createTypeEmail(SUBJECT_TOKEN, template, user.getEmail());

            }

            var digitalSendResponse = sgdBrokerService.sendNotification(digitalSendRequest);

            log.info("Token enviado com sucesso para o usuário {} - Código Envio: {}", user.getUsername(), digitalSendResponse.getSendId());

        } catch (NotFoundException | ClientServiceException | SgdBrokenException e) {
            throw e;

        } catch (Exception e) {
            throw new TokenAccessException("Erro ao enviar token", e);

        }

    }

    public void validateToken(String token, Long idUser) {

        log.info("Validar token para o usuário com ID: {}", idUser);

        try {

            var user = userService.getUserById(idUser);

            if (!token.equals(user.getTokenTemporary())) {
                throw new NegocioException("Token inválido");
            }

        } catch (NotFoundException | NegocioException e) {
            throw e;

        } catch (Exception e) {
            log.error("Erro ao validar o token do usuário", e);
            throw new InternalErrorException("Erro ao validar o token do usuário");

        }

    }

}
