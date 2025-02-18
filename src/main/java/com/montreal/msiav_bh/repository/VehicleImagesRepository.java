package com.montreal.msiav_bh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.VehicleImagesCollection;

@Repository
public interface VehicleImagesRepository extends MongoRepository<VehicleImagesCollection, String> {
	
	Page<VehicleImagesCollection> findByVehicleId(String vehicleId, Pageable pageable);

	Page<VehicleImagesCollection> findByVehicleSeizureId(String vehicleSeizureId, Pageable pageable);
}
