package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    void updateUser(User user);

    List<UserDto> findAllUsers();

    User findUserByResetPasswordToken(String token);
}