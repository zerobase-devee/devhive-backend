package com.devee.devhive.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ;
    private final HttpStatus httpStatus;
    private final String detail;
}
