package com.financetracker.api.request;

import com.financetracker.api.entity.CategoryIcon;
import com.financetracker.api.entity.Role;
import com.financetracker.api.enums.RoleName;
import com.financetracker.api.repository.CategoryIconRepository;
import com.financetracker.api.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final CategoryIconRepository categoryIconRepository;

    @PostConstruct
    public void init() {
        seedRoles();
        seedCategoryIcons();
    }

    private void seedRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
            }
        }
    }

    private void seedCategoryIcons() {
        Map<String, String[]> icons = new HashMap<>();

        icons.put("Housing", new String[]{"🏠", "https://cdn.example.com/icons/housing.png"});
        icons.put("Food", new String[]{"🍽", "https://cdn.example.com/icons/food.png"});
        icons.put("Shopping", new String[]{"🛍", "https://cdn.example.com/icons/shopping.png"});
        icons.put("Salary", new String[]{"💵", "https://cdn.example.com/icons/salary.png"});
        icons.put("Freelance", new String[]{"💼", "https://cdn.example.com/icons/freelance.png"});
        icons.put("Investments", new String[]{"📈", "https://cdn.example.com/icons/investments.png"});
        icons.put("Other", new String[]{"👤", "https://cdn.example.com/icons/other.png"});
        icons.put("Transportation", new String[]{"🚗", "https://cdn.example.com/icons/transportation.png"});
        icons.put("Entertainment", new String[]{"🎮", "https://cdn.example.com/icons/entertainment.png"});
        icons.put("Health", new String[]{"🏥", "https://cdn.example.com/icons/health.png"});
        icons.put("Education", new String[]{"🎓", "https://cdn.example.com/icons/education.png"});
        icons.put("Gifts", new String[]{"🎁", "https://cdn.example.com/icons/gifts.png"});

        for (Map.Entry<String, String[]> entry : icons.entrySet()) {
            String name = entry.getKey();
            String emoji = entry.getValue()[0];
            String url = entry.getValue()[1];

            if (!categoryIconRepository.existsByNameIgnoreCase(name)) {
                CategoryIcon icon = CategoryIcon.builder()
                        .name(name)
                        .emoji(emoji)
                        .iconUrl(url)
                        .build();

                categoryIconRepository.save(icon);
            }
        }
    }

}
