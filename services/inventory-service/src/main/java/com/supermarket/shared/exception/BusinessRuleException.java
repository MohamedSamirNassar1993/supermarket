package com.supermarket.shared.exception;

import com.supermarket.shared.api.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessRuleException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessRuleException(String message) {
        this(ErrorCode.CONFLICT, message);
    }
}
