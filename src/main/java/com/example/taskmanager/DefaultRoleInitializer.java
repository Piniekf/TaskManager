package com.example.taskmanager;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DefaultRoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        // Sprawdzamy, czy role już istnieją w bazie danych, jeśli nie to je utworzy
        if (roleRepository.findByName("ROLE_USER") == null) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
    }
}
