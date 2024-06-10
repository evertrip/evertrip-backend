package com.evertrip.api.response;

import com.evertrip.api.exception.ErrorResponse;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;

    private final T content;

    private final ErrorResponse<T> errorResponse;

    private static final String DEFAULT_SUCCESS_MESSAGE = "Success";

    private static final String DEFAULT_FAIL_MESSAGE = "Fail";

    private ApiResponse(boolean success, T content, ErrorResponse<T> errorResponse) {
        this.success = success;
        this.content = content;
        this.errorResponse = errorResponse;
    }

    public static ApiResponse<String> success() {
        return new ApiResponse<>(true, DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static ApiResponse<String> fail() {
        return new ApiResponse<>(false, DEFAULT_FAIL_MESSAGE, null);
    }

    public static <T> ApiResponse<T> successOf(T content) {
        return new ApiResponse<>(true, content, null);
    }

    public static ApiResponse<ErrorResponse> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(false, null, errorResponse);
    }
}
