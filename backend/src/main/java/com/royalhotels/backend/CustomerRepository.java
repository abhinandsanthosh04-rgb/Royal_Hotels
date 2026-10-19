package com.royalhotels.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * CustomerRepository - Data Access Layer for the 'customers' table.
 *
 * OOP Concept: Interface abstraction. Defines data operations without
 * exposing SQL implementation details.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Find a customer by email address.
     * Useful for checking if a returning guest already has an account.
     */
    Optional<Customer> findByEmail(String email);
}
