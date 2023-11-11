package com.devee.devhive.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomException.CustomExceptionResponse> exceptionHandler(final CustomException e) {
        // CustomExceptionResponse 객체를 생성하여 응답 반환
        return ResponseEntity
            .status(e.getStatus())
            .body(CustomException.CustomExceptionResponse.builder()
                .message(e.getMessage())
                .code(e.getErrorCode().name())
                .status(e.getStatus()).build());
    }

    @ExceptionHandler(OAuthProviderMissMatchException.class)
    public ResponseEntity<ErrorResponse> handleOAuthProviderMissMatchException(OAuthProviderMissMatchException e){
        ErrorResponse errorResponse = new ErrorResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public ProblemDetail getBody() {
                return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        };

        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
    }
}