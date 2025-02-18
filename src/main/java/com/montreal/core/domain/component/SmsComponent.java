package com.montreal.core.domain.component;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SmsComponent {

    public static String getTemplateSendToken(String name, String token) {
        var template = """
            Olá, {{NAME}}!
            
            Seu token de acesso é: {{TOKEN}}
            
            Use este token para concluir seu cadastro no sistema hubRecupera.
            
            Atenciosamente,
            hubRecupera
            """;

        return template.replace("{{NAME}}", name)
                .replace("{{TOKEN}}", token);
    }
}
