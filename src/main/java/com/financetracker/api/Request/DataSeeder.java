package com.financetracker.api.dto;

import com.financetracker.api.entity.Role;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        if (roleRepository.count() == 0) {
            Role admin = Role.builder().name(RoleName.ADMIN).build();
            Role user = Role.builder().name(RoleName.USER).build();
            roleRepository.saveAll(List.of(admin, user));
        }
    }
}
