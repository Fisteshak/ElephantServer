package com.elephant.server.controllers;

import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFolder;
import com.elephant.server.models.FolderFolderId;
import com.elephant.server.repositories.FileRepository;
import com.elephant.server.repositories.FolderFileRepository;
import com.elephant.server.repositories.FolderFolderRepository;
import com.elephant.server.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fs/folder")
public class FolderController {
    @Autowired
    FileRepository fileRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    FolderFileRepository folderFileRepository;
    @Autowired
    FolderFolderRepository folderFolderRepository;


    //returns ID of folder
    @PostMapping
    ResponseEntity<?> createFolder(@RequestParam("id") Integer parentFolderID, @RequestParam("name") String name) {
        try {
            Folder parentFolder = folderRepository.findFolderById(parentFolderID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with id: " + parentFolderID + " not found"));

            if (isFolderNameExists(parentFolder, name)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Folder with the same name already exists");
            }

            Folder childFolder = new Folder(name, parentFolder);
            childFolder = folderRepository.saveAndFlush(childFolder);

            FolderFolderId folderFolderId = new FolderFolderId(parentFolderID, childFolder.getId());
            FolderFolder folderFolder = new FolderFolder(folderFolderId, childFolder.getParent(), childFolder);
            folderFolderRepository.save(folderFolder);

            return new ResponseEntity<>(childFolder.getId(), HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    private boolean isFolderNameExists(Folder parentFolder, String name) {
        List<FolderFolder> parentSubfoldersList = folderFolderRepository.findFolderFoldersByParentFolder(parentFolder);
        for (FolderFolder folder : parentSubfoldersList) {
            if (folder.getChildFolder().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping
    ResponseEntity<?> getFolders(@RequestParam("id") Integer parentFolderID) {
        try {
            Folder parentFolder = folderRepository.findFolderById(parentFolderID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with id: " + parentFolderID + " not found"));
            List<FolderFolder> parentSubfoldersList = folderFolderRepository.findFolderFoldersByParentFolder(parentFolder);
            List<Folder> subfoldersIDs = new ArrayList<>();
            for (FolderFolder folder : parentSubfoldersList) {
                subfoldersIDs.add(folder.getChildFolder());
            }

            return ResponseEntity.ok(subfoldersIDs);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }


}
