package com.montreal.msiav_bh.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import com.montreal.msiav_bh.collection.VehicleCollection;
import com.montreal.msiav_bh.dto.request.VehicleRequest;
import com.montreal.msiav_bh.dto.response.PageVehicleResponse;
import com.montreal.msiav_bh.dto.response.VehicleResponse;
import com.montreal.msiav_bh.enumerations.VehicleStageEnum;
import com.montreal.msiav_bh.enumerations.VehicleStatusEnum;
import com.montreal.msiav_rio.model.response.DataNotificacaoResponse;
import com.montreal.msiav_rio.model.response.VeiculoDTO;

@Mapper(componentModel = "spring")
public interface IVehicleMapper {

    IVehicleMapper INSTANCE = Mappers.getMapper(IVehicleMapper.class);

    VehicleResponse toVehicleResponse(VehicleCollection vehicle);

    @Mapping(target = "id", ignore = true)
    VehicleCollection toVehicle(VehicleRequest request);

    default PageVehicleResponse toCollectionVehicleResponse(Page<VehicleCollection> vehiclePage) {

        var content = vehiclePage.getContent()
                .stream()
                .map(IVehicleMapper.INSTANCE::toVehicleResponse)
                .toList();

        PageVehicleResponse pageVehicleResponse = new PageVehicleResponse();
        pageVehicleResponse.setContent(content);
        pageVehicleResponse.setPageNumber(vehiclePage.getNumber());
        pageVehicleResponse.setPageSize(vehiclePage.getSize());
        pageVehicleResponse.setTotalElements(vehiclePage.getTotalElements());

        return pageVehicleResponse;
    }

    default VehicleRequest toInitialVehicleRequest(DataNotificacaoResponse dataNotificacaoResponse, VeiculoDTO vehicle,
                                                   String numberContract) {

        return VehicleRequest.builder()
                .licensePlate(vehicle.getPlaca())
                .model(vehicle.getModelo())
                .registrationState(vehicle.getUfEmplacamento())
                .creditorName(dataNotificacaoResponse.getNomeCredor())
                .contractNumber(numberContract)
                .stage(VehicleStageEnum.CERTIDAO_BUSCA_APREENSAO_EMITIDA)
                .status(VehicleStatusEnum.A_INICIAR)
                .requestDate(LocalDate.now())
                .lastMovementDate(LocalDateTime.now())
                .build();
    }

}
