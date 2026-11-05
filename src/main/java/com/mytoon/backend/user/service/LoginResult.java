package com.mytoon.backend.user.service;

import com.mytoon.backend.user.entity.User;

public record LoginResult(User user, String refreshToken) {
}
