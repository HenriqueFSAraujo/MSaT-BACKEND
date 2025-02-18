package com.montreal.msiav_bh.mapper;

import com.montreal.msiav_bh.collection.VehicleAddressCollection;
import com.montreal.msiav_bh.dto.ProbableAddressDTO;
import com.montreal.msiav_bh.mapper.base.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProblableAdressMapper extends BaseMapper<VehicleAddressCollection, ProbableAddressDTO> {
}
