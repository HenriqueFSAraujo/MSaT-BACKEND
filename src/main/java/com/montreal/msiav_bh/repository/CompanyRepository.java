package com.montreal.msiav_bh.repository;

import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyCollection, String> {

    Optional<CompanyCollection> findByCompanyType(CompanyTypeEnum companyType);

}
