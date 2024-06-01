package com.elephant.server.repositories;

import com.elephant.server.models.File;
import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFile;
import com.elephant.server.models.FolderFileId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderFileRepository extends JpaRepository<FolderFile, FolderFileId> {
    //List<FolderFile> findFolderFoldersByParentFolder(Folder parentFolder);
    List<FolderFile> findFolderFilesByFolder(Folder parentFolder);
    Optional<FolderFile> findFolderFileByFileId(Integer fileId);
    void deleteByFile(File file);
    void deleteByFile_Id(Integer id);

}
