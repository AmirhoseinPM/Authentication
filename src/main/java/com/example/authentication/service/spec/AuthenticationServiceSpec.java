package com.example.authentication.service.spec;

import com.example.authentication.dto.OtpDto;
import com.example.authentication.dto.RegistrationDto;
import com.example.authentication.dto.TokenDto;
import com.example.authentication.dto.UserDto;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface AuthenticationServiceSpec {
    void sendOtp(RegistrationDto registrationDto);
    Optional<TokenDto> verifyOtp(OtpDto otpDto);
    Optional<UserDto> findMe();
}
