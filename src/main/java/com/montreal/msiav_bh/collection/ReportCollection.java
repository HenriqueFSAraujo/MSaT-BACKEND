package com.montreal.msiav_bh.collection;

import com.montreal.oauth.domain.entity.UserImage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
@Getter
@Setter
@Document(collection = "report")
public class ReportCollection {

    @Id
    private String id;

    @NotNull(message = "SeizureDateId não pode ser nulo")
    private String seizureDateId;

    @NotNull(message = "AddressId não pode ser nulo")
    private String addressId;

    @NotNull(message = "MandateNumber não pode ser nulo")
    @Size(min = 1, max = 50, message = "MandateNumber deve ter entre 1 e 50 caracteres")
    private String mandateNumber;

    @NotNull(message = "MandateDate não pode ser nulo")
    private LocalDate mandateDate;

    @NotNull(message = "DebtorId não pode ser nulo")
    private String debtorId;

    @NotNull(message = "VehicleId não pode ser nulo")
    private String vehicleId;

    private String seizureAddress;

    @NotNull(message = "YardId não pode ser nulo")
    private String yardId;

    @NotNull(message = "CompanyId não pode ser nulo")
    private String companyId;

    private String witnessesId;

    @NotNull(message = "ContractID não pode ser nulo")
    private String contractID;

    @NotNull(message = "ContractNumber não pode ser nulo")
    private String contractNumber;

    @NotNull(message = "DebtValue não pode ser nulo")
    private BigDecimal debtValue;

    @NotNull(message = "TowTruckId não pode ser nulo")
    private String towTruckId;

    @NotNull(message = "VehicleImagesId não pode ser nulo")
    private String vehicleImagesId;

    @NotNull(message = " não pode ser nulo")
    private String notificationId;

    @NotNull(message = " não pode ser nulo")
    private String arNotificationId;

    @NotNull(message = "Debtor não pode ser nulo")
    private DebtorCollection debtor;

    @NotNull(message = "UserImage não pode ser nulo")
    private String userImagesId;
}
