package com.example.authentication.service.impl;

import com.example.authentication.service.spec.OtpServiceSpec;
import org.springframework.stereotype.Service;

@Service
public class OtpService implements OtpServiceSpec {
    @Override
    public void sendOtp(String otp, String phoneNumber){

    }

    @Override
    public String generateOtp() {
        return "1111";
    }
}
