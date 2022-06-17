package com.kale.repository;

import com.kale.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth,Long> {

    boolean existsByEmail(String email);

}
