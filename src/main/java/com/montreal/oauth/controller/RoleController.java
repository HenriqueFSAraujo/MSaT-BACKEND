package com.montreal.oauth.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.montreal.oauth.domain.dto.RoleDTO;
import com.montreal.oauth.domain.entity.Role;
import com.montreal.oauth.domain.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles do Sistema", description = "Listando todas as roles que existem no BD")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDTO> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping("/create")
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO role) {
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.ok(createdRole);
    }
}
