package com.financetracker.api.dto;



import com.financetracker.api.enums.RoleName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    @NotNull(message = "Role name must not be null")
    @NotBlank(message = "Role name must not be blank")
    @Enumerated(EnumType.STRING)
    private RoleName name;
}
