package com.montreal.msiav_bh.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.VehicleCollection;

@Repository
public interface VehicleRepository extends MongoRepository<VehicleCollection, String> {

    Optional<VehicleCollection> findByLicensePlateAndContractNumber(String licensePlate, String contractNumber);

    Optional<VehicleCollection> findByContractNumber(String contractNumber);

    Optional<VehicleCollection> findByLicensePlate(String licensePlate);
    
    Page<VehicleCollection> findByRequestDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

}
