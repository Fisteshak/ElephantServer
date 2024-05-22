package com.elephant.server.service;

import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFile;
import com.elephant.server.models.FolderFolder;
import com.elephant.server.models.FolderFolderId;
import com.elephant.server.repositories.FolderFileRepository;
import com.elephant.server.repositories.FolderFolderRepository;
import com.elephant.server.repositories.FolderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.List;

@Service
public class DatabaseFsService {
    public static boolean isFolderNameExists(Folder parentFolder, String name, FolderFolderRepository folderFolderRepository) {
        List<FolderFolder> parentSubfoldersList = folderFolderRepository.findFolderFoldersByParentFolder(parentFolder);
        for (FolderFolder folder : parentSubfoldersList) {
            if (folder.getChildFolder().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFileExists(Folder parentFolder, String name, FolderFileRepository folderFileRepository) {
        List<FolderFile> parentSubfilesList = folderFileRepository.findFolderFilesByFolder(parentFolder);
        for (FolderFile file : parentSubfilesList) {
            if (file.getFile().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    private static void deleteFolderRec(Integer folderID, FolderFolderRepository folderFolderRepository, FolderRepository folderRepository) throws IOException {
        List<FolderFolder> subfolders = folderFolderRepository.findFolderFoldersByParentFolder(
                folderRepository.findFolderById(folderID).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Folder with id: " + folderID + " not found"))
        );
        folderFolderRepository.deleteAll(subfolders);
        for (FolderFolder folder : subfolders) {
            deleteFolderRec(folder.getChildFolder().getId(), folderFolderRepository, folderRepository);
        }
        String path = getPath(folderID, folderRepository);
        folderRepository.deleteById(folderID);
        FileService.deleteDirectory(FileService.FILES_DIRECTORY + path);

    }

    public static void deleteFolder(Integer folderID, FolderFolderRepository folderFolderRepository, FolderRepository folderRepository) throws IOException {
        if (folderID == 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete root folder");
        }
        Folder parentFolder = folderRepository.findFolderById(folderID).get().getParent();
        folderFolderRepository.deleteById(new FolderFolderId(parentFolder.getId(), folderID));

        deleteFolderRec(folderID, folderFolderRepository, folderRepository);
    }

    public static String getPath(Integer parentID, FolderRepository folderRepository) {
        LinkedList<String> path = new LinkedList<>();
        Folder folder = folderRepository.findFolderById(parentID)
                .orElseThrow(() -> new RuntimeException("Folder with id: " + parentID + " not found"));
        path.add(folder.getName());
        while (folder.getParent() != null) {
            folder = folder.getParent();
            path.addFirst(folder.getName());
        }
        return String.join(FileSystems.getDefault().getSeparator(), path);
    }


}


