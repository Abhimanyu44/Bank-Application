package com.bankapp.repository;

import com.bankapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existByEmail(String email);
    Boolean existByAccountNumber(String accountNumber);
    User findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);


}
