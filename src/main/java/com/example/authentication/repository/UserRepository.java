package com.example.authentication.repository;

import com.example.authentication.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByNationalCode(String nationalCode);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
