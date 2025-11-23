package com.aitsaid.authservice.repositories;

import com.aitsaid.authservice.entities.TokenBlockList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author radouane
 **/
@Repository
public interface TokenBlockListRepository extends JpaRepository<TokenBlockList, Long> {
    Optional<TokenBlockList> findByToken(String token);

    Boolean existsByToken(String token);

}
