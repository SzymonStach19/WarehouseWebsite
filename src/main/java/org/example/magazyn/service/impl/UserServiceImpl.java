package org.example.magazyn.service.impl;

import org.example.magazyn.dto.UserDto;
import org.example.magazyn.entity.User;
import org.example.magazyn.entity.Role;
import org.example.magazyn.repository.RoleRepository;
import org.example.magazyn.repository.UserRepository;
import org.example.magazyn.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        try {
            User user = new User();
            user.setName(userDto.getFirstName() + " " + userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));

            Role role = roleRepository.findByName("ROLE_USER");
            if(role == null){
                role = checkRoleExist();
            }
            user.setRoles(Arrays.asList(role));

            System.out.println("Próba zapisu użytkownika: " + user.getEmail());
            User savedUser = userRepository.save(user);
            System.out.println("Użytkownik zapisany pomyślnie: " + savedUser.getEmail());
        } catch (Exception e) {
            System.err.println("Błąd przy zapisie użytkownika: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Role> newRoles = roleNames.stream()
                .map(name -> {
                    Role role = roleRepository.findByName(name);
                    if (role == null) {
                        role = new Role();
                        role.setName(name);
                        role = roleRepository.save(role);
                    }
                    return role;
                })
                .collect(Collectors.toList());

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    @Override
    public Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setRole(user.getRoles().get(0).getName());
        return userDto;
    }
}