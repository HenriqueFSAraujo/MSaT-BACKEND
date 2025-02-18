package com.montreal.oauth.domain.mapper;

import com.montreal.oauth.domain.dto.RoleDTO;
import com.montreal.oauth.domain.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO roleDTO(Role entity);

    Role role(RoleDTO dto);

    List<RoleDTO> toCollection(List<Role> entities);

}
