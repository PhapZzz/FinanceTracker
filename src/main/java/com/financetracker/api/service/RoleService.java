package com.financetracker.api.service;

import com.financetracker.api.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    public List<RoleDTO> getall();
    public RoleDTO add(RoleDTO roleDTO);
}
