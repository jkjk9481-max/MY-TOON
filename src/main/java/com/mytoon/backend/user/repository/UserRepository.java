package com.mytoon.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mytoon.backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // save , findById , findAll , delete 등 기본 CRUD가 이미 다 포함돼있음.
}
