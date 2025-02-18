package com.montreal.msiav_bh.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.montreal.msiav_bh.collection.AddressCollection;
import com.montreal.msiav_bh.dto.AddressDTO;
import com.montreal.msiav_bh.dto.request.AddressRequest;
import com.montreal.msiav_bh.mapper.base.BaseMapper;

@Mapper(componentModel = "spring")
public interface IAddressMapper extends BaseMapper<AddressCollection, AddressDTO> {

    @Override
    AddressDTO toDto(AddressCollection address);

    @Override
    AddressCollection toEntity(AddressDTO dto);

    @Override
    List<AddressDTO> toDtoList(List<AddressCollection> addresses);

    AddressCollection toEntity(AddressRequest request);
}
