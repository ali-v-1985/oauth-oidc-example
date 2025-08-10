package me.valizadeh.practices.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper using Java record
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp,
    String error
) {
    
    public ApiResponse(boolean success, String message) {
        this(success, message, null, LocalDateTime.now(), null);
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, LocalDateTime.now(), null);
    }
    
    public ApiResponse(boolean success, String message, String error) {
        this(success, message, null, LocalDateTime.now(), error);
    }
    
    // Static factory methods
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(false, message, error);
    }
}
