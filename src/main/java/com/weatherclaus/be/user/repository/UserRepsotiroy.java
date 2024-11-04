package com.weatherclaus.be.user.repository;

import com.weatherclaus.be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepsotiroy extends JpaRepository<User,Long> {

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User findByUsername(String username);

    User findByEmail(String email);

}
