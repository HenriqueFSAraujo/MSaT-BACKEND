package com.montreal.oauth.domain.service;

import com.montreal.core.domain.exception.NegocioException;
import com.montreal.oauth.domain.dto.RoleDTO;
import com.montreal.oauth.domain.entity.Role;
import com.montreal.oauth.domain.mapper.RoleMapper;
import com.montreal.oauth.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(RoleDTO roleDTO) {

        roleRepository.findByName(roleDTO.getName())
                .orElseThrow(() -> new NegocioException(String.format("Role %s Já existe", roleDTO.getName())));

        var role = RoleMapper.INSTANCE.role(roleDTO);

        return roleRepository.save(role);
    }

    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return RoleMapper.INSTANCE.toCollection(roles);
    }

}