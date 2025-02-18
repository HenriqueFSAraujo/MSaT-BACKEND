package com.montreal.oauth.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.montreal.oauth.domain.entity.UserInfo;
import com.montreal.oauth.domain.repository.infrastructure.CustomJpaRepository;


@Repository
public interface IUserRepository extends CustomJpaRepository<UserInfo, Long>, JpaSpecificationExecutor<UserInfo>  {
   
   UserInfo findByUsername(String username);

   UserInfo findFirstById(Long id);

   @Query("SELECT u FROM UserInfo u WHERE u.email = :email")
   UserInfo findByEmail(@Param("email") String email);

   @Query("SELECT u FROM UserInfo u WHERE u.id = :id")
   Optional<UserInfo> findFirstByIdUpdatePassword(@Param("id") Long id);

   @Query("SELECT u FROM UserInfo u WHERE u.link = :link")
   UserInfo findLink(@Param("link") String link);
   
   boolean existsByUsername(String username);

   boolean existsByEmail(String email);
   
   boolean existsByCpf(String cpf);
   
   Page<UserInfo> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
   
   @Query(
	    value = "SELECT * FROM users u " +
	            "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	            "OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	            "OR LOWER(u.cpf) LIKE LOWER(CONCAT('%', :searchTerm, '%'))",
	    countQuery = "SELECT COUNT(*) FROM users u " +
	                 "WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	                 "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	                 "OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	                 "OR LOWER(u.cpf) LIKE LOWER(CONCAT('%', :searchTerm, '%'))",
	    nativeQuery = true
	)
	Page<UserInfo> searchByMultipleFields(@Param("searchTerm") String searchTerm, Pageable pageable);
   
   @Query("SELECT u FROM UserInfo u WHERE u.id = :id")
   Optional<UserInfo> findById(@Param("id") Long id);

   @Query("SELECT u FROM UserInfo u WHERE u.companyId = :companyId")
   List<UserInfo> findAllByCompanyId(@Param("companyId") String companyId);

}
