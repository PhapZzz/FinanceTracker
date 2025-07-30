package com.financetracker.api.dto.response;


import com.financetracker.api.repository.CategoryIconRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidCategoryNameValidator implements ConstraintValidator<ValidCategoryName, String> {

    private final CategoryIconRepository categoryIconRepository;


    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.trim().length() < 2) return false;

        return categoryIconRepository.existsByNameIgnoreCase(name.trim());
    }
}
