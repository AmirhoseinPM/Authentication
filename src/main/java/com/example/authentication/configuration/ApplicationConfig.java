package com.example.authentication.configuration;

import com.example.authentication.exception.UserNotFoundException;
import com.example.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan("com.example.authentication")
public class ApplicationConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;

    @Autowired
    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // sets the new strategy to perform the authentication.
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    UserDetailsService userDetailsService() {
        // how to retrieve the user using the UserRepository that is injected
        return nationalCode -> userRepository.findByNationalCode(nationalCode)
                .orElseThrow(() -> new UserNotFoundException("Account not found"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        // creates an instance of the BCryptPasswordEncoder
        // used to encode the plain user password.
        return new BCryptPasswordEncoder();
    }

}
