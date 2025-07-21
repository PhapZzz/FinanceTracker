package com.financetracker.api.exception;

public class JwtAuthenticationException extends RuntimeException {
  public JwtAuthenticationException(String message) {
    super(message);
  }
}
