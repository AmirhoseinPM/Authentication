package com.example.authentication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createdAt;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^\\+98\\d{10}$",
            message = "Your phone number is not valid!!")
    @Column(unique = true)
    private String phoneNumber;

    @NotNull(message = "National code is required")
    @Pattern(regexp = "^\\d{10}$",
            message = "National code contains 10 number!!")
    @Column(unique = true)
    private String nationalCode;

    private boolean isActive;

    // otp information

    private String otpToken;

    @NotNull(message = "otpCreatedAt is required")
    private Date otpCreatedAt;

    private boolean otpUsed;

    // override methods

    @Override
    public boolean isAccountNonExpired() {
        return isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive();
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return getOtpToken();
    }

    @Override
    public String getUsername() {
        return getNationalCode();
    }
}
