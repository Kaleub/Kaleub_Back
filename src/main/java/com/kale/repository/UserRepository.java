package com.kale.repository;

import com.kale.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT count(u) FROM USER u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

}
