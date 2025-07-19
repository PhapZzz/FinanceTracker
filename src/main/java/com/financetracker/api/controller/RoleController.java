package com.financetracker.api.controller;

import com.financetracker.api.dto.RoleDTO;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<RoleDTO>>> getall(){
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(roleService.getall(),"get role list Success"));
    }


}
