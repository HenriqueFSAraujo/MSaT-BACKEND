package com.montreal.msiav_bh.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.montreal.msiav_bh.collection.HistoryCollection;
import com.montreal.msiav_bh.collection.data.Collected;
import com.montreal.msiav_bh.collection.data.ImpoundLot;
import com.montreal.msiav_bh.collection.data.Location;
import com.montreal.msiav_bh.dto.CollectedDTO;
import com.montreal.msiav_bh.dto.HistoryDTO;
import com.montreal.msiav_bh.dto.ImpoundLotDTO;
import com.montreal.msiav_bh.dto.LocationDTO;
import com.montreal.msiav_bh.dto.request.HistoryRequest;
import com.montreal.msiav_bh.mapper.base.BaseMapper;

@Mapper(componentModel = "spring")
public interface IHistoryMapper extends BaseMapper<HistoryCollection, HistoryDTO> {

    @Override
    HistoryDTO toDto(HistoryCollection entity);

    @Override
    HistoryCollection toEntity(HistoryDTO dto);

    @Override
    List<HistoryDTO> toDtoList(List<HistoryCollection> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "licensePlate", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "creditorName", ignore = true)
    @Mapping(target = "contractNumber", ignore = true)
    HistoryCollection toEntity(HistoryRequest request);

    LocationDTO toLocationDTO(Location location);
    Location toLocation(LocationDTO locationDTO);

    CollectedDTO toCollectedDTO(Collected collected);
    Collected toCollected(CollectedDTO collectedDTO);

    ImpoundLotDTO toImpoundLotDTO(ImpoundLot impoundLot);
    ImpoundLot toImpoundLot(ImpoundLotDTO impoundLotDTO);
}
