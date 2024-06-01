package com.elephant.server.service;

import com.elephant.server.models.*;
import com.elephant.server.repositories.FileRepository;
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


    private static void deleteFolderRec(Integer folderID, FolderFolderRepository folderFolderRepository, FolderRepository folderRepository, FolderFileRepository folderFileRepository, FileRepository fileRepository) throws IOException {


        Folder parentFolder = folderRepository.findFolderById(folderID).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Folder with id: " + folderID + " not found"));
        List<FolderFolder> subfolders = folderFolderRepository.findFolderFoldersByParentFolder(
                parentFolder
        );


        folderFolderRepository.deleteAll(subfolders);
        for (FolderFolder folder : subfolders) {
            deleteFolderRec(folder.getChildFolder().getId(), folderFolderRepository, folderRepository, folderFileRepository, fileRepository);
        }
        String path = getFolderPath(folderID, folderRepository);

        List<FolderFile> subfiles = folderFileRepository.findFolderFilesByFolder(parentFolder);

        //delete files
        for (FolderFile folderFile: subfiles) {
            deleteFile(folderFile.getFile().getId(), folderFileRepository, fileRepository);
        }

        folderRepository.deleteById(folderID);
        FileService.deleteDirectory(FileService.FILES_DIRECTORY + path);

    }

    public static void deleteFile(Integer fileID, FolderFileRepository folderFileRepository, FileRepository fileRepository) throws IOException {
        File file = fileRepository.findFileById(fileID).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Didn't find file with id: " + fileID)
        );
        String filepath = getFilePath(file.getId(), fileRepository);
        FileService.deleteFile(FileService.FILES_DIRECTORY + filepath);

        folderFileRepository.deleteById(new FolderFileId(file.getParent().getId(), fileID));
        fileRepository.deleteById(fileID);
    }


    public static void deleteFolder(Integer folderID, FolderFolderRepository folderFolderRepository, FolderRepository folderRepository, FolderFileRepository folderFileRepository, FileRepository fileRepository) throws IOException {
        if (folderID == 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete root folder");
        }
        Folder parentFolder = folderRepository.findFolderById(folderID).get().getParent();
        folderFolderRepository.deleteById(new FolderFolderId(parentFolder.getId(), folderID));

        deleteFolderRec(folderID, folderFolderRepository, folderRepository, folderFileRepository, fileRepository);
    }

    public static String getFolderPath(Integer parentID, FolderRepository folderRepository) {
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

    public static String getFilePath(Integer parentID, FileRepository fileRepository) {
        LinkedList<String> path = new LinkedList<>();
        File file = fileRepository.findFileById(parentID)
                .orElseThrow(() -> new RuntimeException("Folder with id: " + parentID + " not found"));
        path.addFirst(file.getName());

        Folder folder = file.getParent();

        while (folder != null) {
            path.addFirst(folder.getName());
            folder = folder.getParent();
        }
        return String.join(FileSystems.getDefault().getSeparator(), path);
    }


}


