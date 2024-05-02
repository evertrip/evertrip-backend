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

    // LOGIN
    INVALID_SOCIAL_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "Unsupported or invalid social login type"),

    // AUTHORITY

    AUTHORITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Authority not founded"),

    // TOKEN
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"User Authentication is failed"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken is invalid"),

    // MEMBER
    INCORRECT_FORMAT_NICKNAME(HttpStatus.UNPROCESSABLE_ENTITY, "Nickname format is Incorrect"),
    INCORRECT_FORMAT_DESCRIPTION(HttpStatus.UNPROCESSABLE_ENTITY, "Description format is Incorrect"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),

    // File
    INVALID_FILE_TYPE(HttpStatus.FORBIDDEN, "Invalid file type"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not founded"),
    FILE_INFO_EXISTS(HttpStatus.CONFLICT, "File Info exists"),
    MAX_FILE_SIZE_10MB(HttpStatus.BAD_REQUEST, "Max file size 10MB"),
    FILE_STORAGE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "File storage failed"),

    // POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not founded"),

    INCORRECT_FORMAT_POST(HttpStatus.UNPROCESSABLE_ENTITY, "Post format is Incorrect"),

    NOT_WRITER(HttpStatus.FORBIDDEN, "Not Writer of the Post" );

    private HttpStatus status;
    private String message;
}