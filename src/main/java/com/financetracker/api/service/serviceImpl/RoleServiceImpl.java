package com.financetracker.api.service.serviceImpl;

import com.financetracker.api.dto.RoleDTO;
import com.financetracker.api.entity.Role;
import com.financetracker.api.mapper.RoleMapper;
import com.financetracker.api.repository.RoleRepository;
import com.financetracker.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository,RoleMapper roleMapper){
        this.roleMapper = roleMapper;
        this.roleRepository = roleRepository;
    }



    @Override
    public List<RoleDTO> getall(){


        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Override
    public RoleDTO add(RoleDTO roleDTO){

        return roleMapper.toDTO(roleRepository.save(roleMapper.toEntity(roleDTO)));
    }
}
