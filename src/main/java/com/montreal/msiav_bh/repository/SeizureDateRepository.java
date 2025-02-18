package com.montreal.msiav_bh.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.montreal.msiav_bh.collection.SeizureDateCollection;

@Repository
public interface SeizureDateRepository extends MongoRepository<SeizureDateCollection, String> {
    List<SeizureDateCollection> findByVeiculeId(String vehicleId);
}


