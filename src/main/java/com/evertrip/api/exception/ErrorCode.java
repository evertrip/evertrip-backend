package com.evertrip.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // COMMON
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Permission is invalid"),
    ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "Access forbidden"),
    STATUS_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "status value not founded"),
    INVALID_VALUE(HttpStatus.FORBIDDEN, "Invalid value"),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "Invalid enum value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    CRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Crypt error"),

    // TOKEN
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"User Authentication is failed"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken is invalid");

    private HttpStatus status;
    private String message;
}