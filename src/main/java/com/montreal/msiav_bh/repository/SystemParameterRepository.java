package com.montreal.msiav_bh.repository;

import com.montreal.msiav_bh.collection.SystemParameterCollection;
import com.montreal.oauth.domain.repository.infrastructure.CustomJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemParameterRepository extends CustomJpaRepository<SystemParameterCollection, Long> {

    Optional<SystemParameterCollection> findBySystemAndParameter(String system, String parameter);

}
