package com.elephant.server.repositories;

import com.elephant.server.CloudUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CloudUser, Integer> {
    CloudUser findUserById(Integer id);
    Optional<CloudUser> findUserByLogin(String login);
}
