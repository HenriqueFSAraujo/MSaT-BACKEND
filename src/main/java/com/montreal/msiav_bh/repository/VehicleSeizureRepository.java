package com.montreal.msiav_bh.repository;

import com.montreal.msiav_bh.collection.VehicleSeizureCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleSeizureRepository extends MongoRepository<VehicleSeizureCollection, String> {
	
}
