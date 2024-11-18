package com.example.authentication.controller;

import com.example.authentication.dto.OtpDto;
import com.example.authentication.dto.RegistrationDto;
import com.example.authentication.dto.TokenDto;
import com.example.authentication.dto.UserDto;
import com.example.authentication.exception.UserNotFoundException;
import com.example.authentication.service.spec.AuthenticationServiceSpec;
import com.example.authentication.service.spec.ErrorResponseServiceSpec;
import com.example.authentication.validation.RegistrationValidation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AuthenticationServiceSpec authenticationService;
    private final RegistrationValidation registrationValidation;
    private final ErrorResponseServiceSpec errorResponseService;

    public UserController(
            AuthenticationServiceSpec authenticationService,
            ErrorResponseServiceSpec errorResponseService,
            RegistrationValidation registrationValidation) {
        this.authenticationService = authenticationService;
        this.registrationValidation = registrationValidation;
        this.errorResponseService = errorResponseService;
    }

    @PostMapping(value = "/register",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    public ResponseEntity<Object> registerUser(
            @Valid @RequestBody RegistrationDto registrationDto,
            BindingResult bindingResult) {

        // edit phoneNumber to starts with +98
        String phoneNumber = registrationDto.getPhoneNumber();
        if (phoneNumber.startsWith("0"))
            phoneNumber = "+98".concat(
                    phoneNumber.substring(1));
        registrationDto.setPhoneNumber(phoneNumber);

        // check annotation validation in RegistrationDto
        if (bindingResult.hasErrors())
            return errorResponseService.returnValidationError(bindingResult);

        // check phoneNumber and NationalCode matching
        registrationValidation.validate(registrationDto, bindingResult);
        if (bindingResult.hasErrors())
            return errorResponseService.returnValidationError(bindingResult);

        authenticationService.sendOtp(registrationDto);

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body("OTP token send successfully");
    }

    @PostMapping(value = "/otp",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> verifyOtp(
            @Valid @RequestBody OtpDto otpDto,
            BindingResult bindingResult
            ) {

        // edit phoneNumber to starts with +98
        String phoneNumber = otpDto.getPhoneNumber();
        if (phoneNumber.startsWith("0"))
            phoneNumber = "+98".concat(
                    phoneNumber.substring(1));
        otpDto.setPhoneNumber(phoneNumber);

        // check annotation validation in otpDto
        if (bindingResult.hasErrors())
            return errorResponseService.returnValidationError(bindingResult);

        TokenDto tokenDto = authenticationService.verifyOtp(otpDto)
                .orElseThrow(
                        () -> new ValidationException("Failed to verify otp!")
                );
        return ResponseEntity.ok(tokenDto);
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto findMe() {
        return authenticationService.findMe().orElseThrow(
                () -> new UserNotFoundException("failed to find you based on authorization field")
        );
    }

}
