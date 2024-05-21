package com.elephant.server.repositories;

import com.elephant.server.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Integer> {
    Optional<Folder> findFolderByName(String name);

    Optional<Folder> findFolderById(Integer id);
}
