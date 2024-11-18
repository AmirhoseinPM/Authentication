package com.example.authentication.service.impl;

import com.example.authentication.dto.OtpDto;
import com.example.authentication.dto.RegistrationDto;
import com.example.authentication.dto.TokenDto;
import com.example.authentication.dto.UserDto;
import com.example.authentication.exception.UserNotFoundException;
import com.example.authentication.exception.ValidationException;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.security.JwtService;
import com.example.authentication.service.spec.AuthenticationServiceSpec;
import com.example.authentication.service.spec.OtpServiceSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService implements AuthenticationServiceSpec {


    private final OtpServiceSpec otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthenticationService(OtpServiceSpec otpService,
                                 UserRepository userRepository,
                                 JwtService jwtService,
                                 PasswordEncoder passwordEncoder) {
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void sendOtp(RegistrationDto registrationDto) {
        String otp = otpService.generateOtp();
//        log.info(registrationDto.toString());
        User user = userRepository.findByNationalCode(
                registrationDto.getNationalCode()).orElse(null);
//        log.info(user.toString());

        if (user == null) {
            user = new User();
            user.setOtpCreatedAt(Date.from(Instant.now()));
            user.setOtpToken(passwordEncoder.encode(otp));
            user.setOtpUsed(false);
            user.setNationalCode(registrationDto.getNationalCode());
            user.setPhoneNumber(registrationDto.getPhoneNumber());
//            log.info(user.toString());

        } else {
            // check last time that otp sent to user
            long otpDuration =  Instant.now().toEpochMilli() - user.getOtpCreatedAt().toInstant().toEpochMilli();
            log.info(String.format("%d", otpDuration));
            if ((otpDuration < 120_000) && (!user.isOtpUsed()))
                throw new ValidationException("Otp sent recently, try again 2 minutes later!");

            // create new otp for user
            user.setOtpCreatedAt(Date.from(Instant.now()));
            user.setOtpToken(passwordEncoder.encode(otp));
            user.setOtpUsed(false);
//            log.info(user.toString());
        }
        // save user info -> old with new otp information | totally new
        try {
            user = userRepository.save(user);
        } catch (Exception ex) {
            throw new ValidationException("PhoneNumber already exists.");
        }
//        log.info(user.toString());
        // send otp token to user's phoneNumber
        otpService.sendOtp(otp, user.getPhoneNumber());
    }

    @Override
    public Optional<TokenDto> verifyOtp(OtpDto otpDto) {
        User user = userRepository.findByPhoneNumber(otpDto.getPhoneNumber())
                .orElseThrow(
                        () -> new ValidationException("User with phone number not registered")
                );
        // check userOtp match with otp
        if (!passwordEncoder.matches(otpDto.getOtp(), user.getOtpToken()))
            throw new ValidationException("Failed to validate otp with phone number");

        // every otp token is acceptable just for 2 minutes
        // and can use for 1 time
        long otpDuration =  Instant.now().toEpochMilli() - user.getOtpCreatedAt().toInstant().toEpochMilli();
        log.info(String.format("%d", otpDuration));
        if (user.isOtpUsed() || (otpDuration > 120_000))
            throw new ValidationException("Otp is not validate anymore!");

        // Activate user
        user.setActive(true);
        // change otpUsed state
        user.setOtpUsed(true);
        user = userRepository.save(user);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(jwtService.generateToken(user));
        tokenDto.setExpiresIn(jwtService.getExpirationTime().toString());


        return Optional.of(tokenDto);
    }

    @Override
    public Optional<UserDto> findMe() {
        // get Authentication Bean from context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByNationalCode(authentication.getName())
                .orElseThrow(
                        () -> new UserNotFoundException("User with phone number not registered")
                );
        UserDto userDto = new UserDto();
        userDto.setNationalCode(user.getNationalCode());
        userDto.setPhoneNumber(user.getPhoneNumber());

        return Optional.of(userDto);
    }
}
