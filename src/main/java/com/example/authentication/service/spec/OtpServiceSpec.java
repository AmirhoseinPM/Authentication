package com.example.authentication.service.spec;

public interface OtpServiceSpec {
    void sendOtp(String otp, String phoneNumber);
    String generateOtp();
}
