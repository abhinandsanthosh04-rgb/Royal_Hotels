package com.royalhotels.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * AdminRepository - Data Access Layer for the 'admins' table.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsername(String username);

    boolean existsByUsername(String username);
}
