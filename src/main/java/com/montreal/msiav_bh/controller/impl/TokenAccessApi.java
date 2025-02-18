package com.montreal.msiav_bh.controller.impl;

import com.montreal.msiav_bh.controller.ITokenAccessApi;
import com.montreal.msiav_bh.service.TokenAccessService;
import com.montreal.oauth.domain.enumerations.TwoFactorTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TokenAccessApi implements ITokenAccessApi {

    private final TokenAccessService tokenAccessService;

    @Override
    public void sendToken(Long idUser, TwoFactorTypeEnum type) {

        tokenAccessService.sendToken(idUser, type);

    }

    @Override
    public void validateToken(@PathVariable String token, @PathVariable Long idUser) {

        tokenAccessService.validateToken(token, idUser);

    }

}
