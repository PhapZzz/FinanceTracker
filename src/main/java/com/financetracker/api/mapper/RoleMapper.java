package com.financetracker.api.mapper;


import com.financetracker.api.dto.RoleDTO;
import com.financetracker.api.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")

public interface RoleMapper {

public RoleDTO toDTO(Role role);

public Role toEntity(RoleDTO roleDTO);

}
