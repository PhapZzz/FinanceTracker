package com.financetracker.api.controller;

import com.financetracker.api.dto.RoleDTO;
import com.financetracker.api.response.SuccessResponse;
import com.financetracker.api.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @GetMapping
    public ResponseEntity<SuccessResponse<List<RoleDTO>>> getall(){
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(roleService.getall(),"get role list Success"));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<RoleDTO>> add(@Valid @RequestBody RoleDTO roleDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(roleService.add(roleDTO),"add role Success"));
    }


}
