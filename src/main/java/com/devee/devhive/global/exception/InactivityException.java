package com.devee.devhive.global.exception;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class InactivityException extends InternalAuthenticationServiceException {

  public InactivityException(String msg) {
    super(msg);
  }
}
