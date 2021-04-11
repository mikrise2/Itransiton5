package com.example.demo.repos;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String username);

    @Transactional
    void deleteByEmail(String username);
}