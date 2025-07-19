package com.financetracker.api.dto;



import com.financetracker.api.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private RoleName name;
}
