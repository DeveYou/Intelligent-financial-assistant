package com.aitsaid.authservice.repositories;

import com.aitsaid.authservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author radouane
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCin(String cin);

    Boolean existsByEmail(String email);


    boolean existsByCin(String cin);
}
