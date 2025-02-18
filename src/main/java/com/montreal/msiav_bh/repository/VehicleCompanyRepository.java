package com.montreal.msiav_bh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.VehicleCompanyCollection;

@Repository
public interface VehicleCompanyRepository extends MongoRepository<VehicleCompanyCollection, String> {

    @Query("{ 'vehicleId': ?0, 'companyId': ?1 }")
    Optional<VehicleCompanyCollection> findByVehicleIdAndCompanyId(String vehicleId, String companyId);

    Optional<List<VehicleCompanyCollection>> findByVehicleId(String vehicleId);
    
    List<VehicleCompanyCollection> findAllByVehicleIdAndCompanyId(String vehicleId, String companyId);
    
    @Query("{ 'vehicleId': ?0 }")
    List<VehicleCompanyCollection> findVehicleCompaniesByVehicleId(String vehicleId);
}
