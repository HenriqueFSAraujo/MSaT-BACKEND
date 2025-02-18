package com.montreal.core.domain.helper;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

@UtilityClass
public class GenerationTokenHelper {

    private static final int TOKEN_LENGTH = 12;

    /**
     * Generates a temporary token with 12 characters consisting of
     * uppercase letters, lowercase letters, and digits.
     *
     * @return A randomly generated token.
     */
    public static String generateToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    public static LocalDateTime generateTokenExpiredAt() {
        return LocalDateTime.now().plusDays(10);
    }

}
