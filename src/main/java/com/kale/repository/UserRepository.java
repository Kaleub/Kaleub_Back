package com.kale.repository;

import com.kale.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    //email이 존재할 경우 true, 존재하지 않을 경우 false가 리턴됨
    boolean existsByEmail(String email);

}
