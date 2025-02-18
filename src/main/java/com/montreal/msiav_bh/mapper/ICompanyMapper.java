package com.montreal.msiav_bh.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.dto.request.CompanyRequest;
import com.montreal.msiav_bh.mapper.base.BaseMapper;

@Mapper(componentModel = "spring", uses = IAddressMapper.class)
public interface ICompanyMapper extends BaseMapper<CompanyCollection, CompanyDTO> {

    @Override
    CompanyDTO toDto(CompanyCollection entity);

    @Override
    CompanyCollection toEntity(CompanyDTO dto);

    @Override
    List<CompanyDTO> toDtoList(List<CompanyCollection> entities);

    @Mapping(target = "id", ignore = true)
    CompanyCollection toEntity(CompanyRequest request);
}
