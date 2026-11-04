package com.mytoon.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
