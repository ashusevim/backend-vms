package com.vms.repository;

import com.vms.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Visitor} entity CRUD and query operations.
 *
 * <p>Provides lookup methods by mobile number, email, name, and identity proof.
 * Also supports a keyword-based search across multiple fields.</p>
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    /**
     * Finds a visitor by their mobile number.
     *
     * @param mobileNumber the visitor's mobile number
     * @return an {@link Optional} containing the visitor if found
     */
    Optional<Visitor> findByMobileNumber(String mobileNumber);

    /**
     * Finds a visitor by their email address.
     *
     * @param email the visitor's email
     * @return an {@link Optional} containing the visitor if found
     */
    Optional<Visitor> findByEmail(String email);

    /**
     * Searches for visitors whose name contains the given string (case-insensitive).
     *
     * @param name the search term
     * @return a list of matching visitors
     */
    List<Visitor> findByNameContainingIgnoreCase(String name);

    /**
     * Searches for visitors matching the keyword across name, mobile number, and email.
     *
     * @param keyword the search keyword
     * @return a list of matching visitors
     */
    @Query("SELECT v FROM Visitor v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR v.mobileNumber LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(v.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Visitor> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Finds a visitor by their identity proof type and number combination.
     *
     * @param idProofType   the type of ID proof
     * @param idProofNumber the ID proof number
     * @return an {@link Optional} containing the visitor if found
     */
    Optional<Visitor> findByIdProofTypeAndIdProofNumber(
            com.vms.enums.IdProofType idProofType, String idProofNumber);
}
