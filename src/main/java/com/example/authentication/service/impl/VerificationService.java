package com.example.authentication.service.impl;

import com.example.authentication.service.spec.VerificationServiceSpec;
import org.springframework.stereotype.Service;

@Service
public class VerificationService implements VerificationServiceSpec {
    @Override
    public boolean isVerified(String nationalCode, String phoneNumber) {
        return true;
    }
}
