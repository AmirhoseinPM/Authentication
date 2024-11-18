package com.example.authentication.validation;

import com.example.authentication.dto.RegistrationDto;
import com.example.authentication.service.spec.VerificationServiceSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class RegistrationValidation implements Validator {

    private final VerificationServiceSpec verificationServiceSpec;

    public RegistrationValidation(VerificationServiceSpec verificationServiceSpec) {
        this.verificationServiceSpec = verificationServiceSpec;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationDto registrationDto = (RegistrationDto) target;
        if (!verificationServiceSpec.isVerified(
                registrationDto.getNationalCode(),
                registrationDto.getPhoneNumber())
        )
                errors.reject("VerificationFailed", "National code and phoneNumber is not matched!");
    }
}
