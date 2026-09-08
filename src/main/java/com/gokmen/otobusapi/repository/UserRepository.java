package com.gokmen.otobusapi.repository;

import com.gokmen.otobusapi.repository.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
