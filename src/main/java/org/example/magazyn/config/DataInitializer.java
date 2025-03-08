package org.example.magazyn.config;

import org.example.magazyn.entity.Role;
import org.example.magazyn.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> roles = List.of(
                Role.ROLE_USER,
                Role.ROLE_ADMIN,
                Role.ROLE_WAREHOUSEMAN,
                Role.ROLE_MANAGER
        );

        for (String roleName : roles) {
            if (roleRepository.findByName(roleName) == null) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}
