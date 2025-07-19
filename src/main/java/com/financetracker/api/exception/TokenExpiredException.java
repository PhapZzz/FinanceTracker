package com.financetracker.api.exception;


public class TokenExpiredException extends RuntimeException { public TokenExpiredException(String message) {
     super(message);
     }
}
