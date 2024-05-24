package com.elephant.server.repositories;

import com.elephant.server.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Integer> {
    Optional<File> findFileById(Integer id);
}
