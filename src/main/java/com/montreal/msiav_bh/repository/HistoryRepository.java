package com.montreal.msiav_bh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.HistoryCollection;

import java.util.List;

@Repository
public interface HistoryRepository extends MongoRepository<HistoryCollection, String> {

    List<HistoryCollection> findByIdVehicle(String vehicleId);

}
