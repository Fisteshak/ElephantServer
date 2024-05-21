package com.elephant.server.repositories;

import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFolder;
import com.elephant.server.models.FolderFolderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderFolderRepository extends JpaRepository<FolderFolder, FolderFolderId> {
    List<FolderFolder> findFolderFoldersByParentFolder(Folder parentFolder);
}
