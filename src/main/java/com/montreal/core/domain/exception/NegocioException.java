package com.montreal.core.domain.exception;

import com.montreal.core.exception_handler.ProblemType;
import lombok.Getter;

@Getter
public class NegocioException extends RuntimeException {

    private final ProblemType problemType;

    public NegocioException(String message) {
        super(message);
        this.problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
    }

    public NegocioException(ProblemType problemType, String message) {
        super(message);
        this.problemType = problemType;
    }

    public NegocioException(ProblemType problemType, String message, Throwable cause) {
        super(message, cause);
        this.problemType = problemType;
    }

}
