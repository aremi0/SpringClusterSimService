package com.aremi.springclustersimservice.repository;

import com.aremi.springclustersimservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Ritorna la password hashata (BCrypt) da una ricerca dello username
     * @param username Parametro di ricerca in tabella
     * @return Password hashata dell'utente
     */
    @Query("SELECT u.hashedPassword FROM UserEntity u WHERE u.username = :username")
    String findHashedPasswordByUsername(@Param("username") String username);

    boolean existsByUsername(String username);
}
