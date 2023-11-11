package com.devee.devhive.global.exception;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class OAuthProviderMissMatchException extends InternalAuthenticationServiceException {

  public OAuthProviderMissMatchException(String message) {
    super(message);
  }
}
