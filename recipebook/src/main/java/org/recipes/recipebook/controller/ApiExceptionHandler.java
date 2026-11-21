package org.recipes.recipebook.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import lombok.NonNull;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(@NonNull MethodArgumentNotValidException ex, @NonNull WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(this::formatFieldError)
            .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(@NonNull IllegalArgumentException ex, @NonNull WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, detailOrDefault(ex.getMessage(), HttpStatus.BAD_REQUEST), List.of(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(@NonNull ResponseStatusException ex, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = detailOrDefault(ex.getReason(), status);
        return build(status, message, List.of(), request);
    }

    private ResponseEntity<ProblemDetail> build(@NonNull HttpStatus status, @NonNull String detail,
            @NonNull List<String> errors, @NonNull WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setInstance(URI.create(path(request)));
        if (!errors.isEmpty()) {
            problem.setProperty("errors", errors);
        }
        return ResponseEntity.status(status).body(problem);
    }

    private String detailOrDefault(String detail, HttpStatus status) {
        return detail == null || detail.isBlank() ? status.getReasonPhrase() : detail;
    }

    private String formatFieldError(@NonNull FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private String path(@NonNull WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "/";
    }
}
