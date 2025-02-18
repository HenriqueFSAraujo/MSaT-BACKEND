package com.montreal.msiav_bh.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.AddressCollection;

@Repository
public interface AddressRepository extends MongoRepository<AddressCollection, String> {
	
	List<AddressCollection> findByPostalCodeAndStreetAndNumberAndNeighborhoodAndCity(
            String postalCode,
            String street,
            String number,
            String neighborhood,
            String city
    );
	
}
