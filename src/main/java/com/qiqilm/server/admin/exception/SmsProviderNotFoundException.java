package com.qiqilm.server.admin.exception;

public class SmsProviderNotFoundException extends RuntimeException {
    public SmsProviderNotFoundException(String message) {
        super(message);
    }
}
