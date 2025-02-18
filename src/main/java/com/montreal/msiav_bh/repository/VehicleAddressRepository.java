package com.montreal.msiav_bh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.VehicleAddressCollection;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleAddressRepository extends MongoRepository<VehicleAddressCollection, String> {

    Optional<VehicleAddressCollection> findByVehicleIdAndAddressId(String vehicleId, String addressId);

    Optional<List<VehicleAddressCollection>> findByVehicleId(String vehicleId);

}
