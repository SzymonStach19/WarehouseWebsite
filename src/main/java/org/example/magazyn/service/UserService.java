package org.example.magazyn.service;

import org.example.magazyn.dto.UserDto;
import org.example.magazyn.entity.User;
import org.example.magazyn.entity.Role;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();
    void updateUserRoles(Long userId, List<String> roleNames);
    Role checkRoleExist();
}