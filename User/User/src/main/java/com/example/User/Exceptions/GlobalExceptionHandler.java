package com.example.User.Exceptions;

import com.example.User.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.xml.crypto.Data;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* -------------------- JSON / ObjectMapper Errors -------------------- */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidJson(HttpMessageNotReadableException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Invalid request body. Please check JSON format or data types.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* -------------------- Validation Errors -------------------- */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* -------------------- Invalid Params -------------------- */

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Invalid value for parameter: " + ex.getName())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingParam(
            MissingServletRequestParameterException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Missing request parameter: " + ex.getParameterName())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* -------------------- Business Exceptions -------------------- */

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(DataNotFoundException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentials(InvalidCredentialsException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("You do not have permission to perform this action.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /* -------------------- Fallback -------------------- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("Something went wrong on the server. Please try again later.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
