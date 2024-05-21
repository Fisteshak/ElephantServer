package com.elephant.server.repositories;

import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFile;
import com.elephant.server.models.FolderFileId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderFileRepository extends JpaRepository<FolderFile, FolderFileId> {
    //List<FolderFile> findFolderFoldersByParentFolder(Folder parentFolder);
    List<FolderFile> findFolderFilesByFolder(Folder parentFolder);

}
