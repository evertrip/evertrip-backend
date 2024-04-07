package com.evertrip.api.exception;

import lombok.Getter;
@Getter
public class ErrorResponse<T> {

    private Integer statusCode;

    private String statusPhrase;

    private String errorName;
    private String errorMessage;



    public ErrorResponse(ErrorCode errorCode) {
        this.statusCode = errorCode.getStatus().value();
        this.statusPhrase = errorCode.getStatus().getReasonPhrase();
        this.errorName = errorCode.name();
        this.errorMessage = errorCode.getMessage();

    }

    public static <T> ErrorResponse<T> of(ErrorCode errorCode) {
        return new ErrorResponse<>(errorCode);
    }



}