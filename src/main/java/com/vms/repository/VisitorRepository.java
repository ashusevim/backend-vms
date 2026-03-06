package com.vms.repository;

import com.vms.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByMobileNumber(String mobileNumber);

    Optional<Visitor> findByEmail(String email);

    List<Visitor> findByNameContainingIgnoreCase(String name);

    @Query("SELECT v FROM Visitor v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR v.mobileNumber LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(v.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Visitor> searchByKeyword(@Param("keyword") String keyword);

    Optional<Visitor> findByIdProofTypeAndIdProofNumber(
            com.vms.enums.IdProofType idProofType, String idProofNumber);
}
