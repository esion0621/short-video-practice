package com.video.backend.service;

import com.video.backend.dto.UserLoginDTO;
import com.video.backend.dto.UserRegisterDTO;
import com.video.backend.entity.User;

public interface UserService {
    String register(UserRegisterDTO dto);
    String login(UserLoginDTO dto);
    User getUserById(Long userId);
    User getUserByUsername(String username);
}
