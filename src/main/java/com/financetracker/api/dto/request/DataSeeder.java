package com.financetracker.api.dto.request;

import com.financetracker.api.entity.Role;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;

//    @PostConstruct
//    public void seedRoles() {
//        if (roleRepository.count() == 0) {
//            Role admin = Role.builder().name(RoleName.ADMIN).build();
//            Role user = Role.builder().name(RoleName.USER).build();
//            roleRepository.saveAll(List.of(admin, user));
//        }
//
//    }
    @PostConstruct
    public void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            // Nếu chưa có role này trong DB thì mới tạo
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder()
                            .name(roleName)
                            .build();
                roleRepository.save(role);
            }
        }
    }
}
