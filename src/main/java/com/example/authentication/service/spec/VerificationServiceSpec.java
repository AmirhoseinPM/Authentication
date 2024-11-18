package com.example.authentication.service.spec;

public interface VerificationServiceSpec {
    boolean isVerified(String nationalCode, String phoneNumber);
}
