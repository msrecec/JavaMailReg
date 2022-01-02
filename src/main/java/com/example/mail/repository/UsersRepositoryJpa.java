package com.example.mail.repository;

import com.example.mail.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepositoryJpa extends JpaRepository<User, Long> {

}
