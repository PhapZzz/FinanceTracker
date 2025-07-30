package com.financetracker.api.repository;

import com.financetracker.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //do dang ky la email nen
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User findByid(Long id);

}
