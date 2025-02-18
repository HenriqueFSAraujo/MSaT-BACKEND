package com.montreal.msiav_bh.mapper;

import com.montreal.msiav_bh.collection.ReportCollection;
import com.montreal.msiav_bh.dto.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IReportMapper {

    IReportMapper INSTANCE = Mappers.getMapper(IReportMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "seizureDateId", source = "seizureDateId")
    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "mandateNumber", source = "mandateNumber")
    @Mapping(target = "mandateDate", source = "mandateDate")
    @Mapping(target = "debtorId", source = "debtorId")
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "seizureAddress", source = "seizureAddress")
    @Mapping(target = "yardId", source = "yardId")
    @Mapping(target = "companyId", source = "companyId")
    @Mapping(target = "witnessesId", source = "witnessesId")
    @Mapping(target = "contractID", source = "contractID")
    @Mapping(target = "contractNumber", source = "contractNumber")
    @Mapping(target = "debtValue", source = "debtValue")
    @Mapping(target = "towTruckId", source = "towTruckId")
    @Mapping(target = "vehicleImagesId", source = "vehicleImagesId")
    @Mapping(target = "notificationId", source = "notificationId")
    @Mapping(target = "arNotificationId", source = "arNotificationId")
    @Mapping(target = "debtor", source = "debtor")
    @Mapping(target = "userImagesId", source = "userImagesId")
    ReportDTO toDTO(ReportCollection reportCollection);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "seizureDateId", source = "seizureDateId")
    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "mandateNumber", source = "mandateNumber")
    @Mapping(target = "mandateDate", source = "mandateDate")
    @Mapping(target = "debtorId", source = "debtorId")
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "seizureAddress", source = "seizureAddress")
    @Mapping(target = "yardId", source = "yardId")
    @Mapping(target = "companyId", source = "companyId")
    @Mapping(target = "witnessesId", source = "witnessesId")
    @Mapping(target = "contractID", source = "contractID")
    @Mapping(target = "contractNumber", source = "contractNumber")
    @Mapping(target = "debtValue", source = "debtValue")
    @Mapping(target = "towTruckId", source = "towTruckId")
    @Mapping(target = "vehicleImagesId", source = "vehicleImagesId")
    @Mapping(target = "notificationId", source = "notificationId")
    @Mapping(target = "arNotificationId", source = "arNotificationId")
    @Mapping(target = "debtor", source = "debtor")
    @Mapping(target = "userImagesId", source = "userImagesId")
    ReportCollection toEntity(ReportDTO reportDTO);
}
