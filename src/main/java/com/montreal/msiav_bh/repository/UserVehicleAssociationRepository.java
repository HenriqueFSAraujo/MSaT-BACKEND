package com.montreal.msiav_bh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.UserVehicleAssociationCollection;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVehicleAssociationRepository extends MongoRepository<UserVehicleAssociationCollection, String> {

    Optional<UserVehicleAssociationCollection> findByUserIdAndVehicleId(Long userId, String vehicleId);

    List<UserVehicleAssociationCollection> findAllByUserId(String userId);

    List<UserVehicleAssociationCollection> findAllByVehicleId(String vehicleId);

    boolean existsByUserIdAndVehicleId(Long userId, String vehicleId);
}
