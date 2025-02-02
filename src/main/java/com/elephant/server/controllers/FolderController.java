package com.elephant.server.controllers;

import com.elephant.server.models.*;
import com.elephant.server.repositories.FileRepository;
import com.elephant.server.repositories.FolderFileRepository;
import com.elephant.server.repositories.FolderFolderRepository;
import com.elephant.server.repositories.FolderRepository;
import com.elephant.server.service.DatabaseFsService;
import com.elephant.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

            if (DatabaseFsService.isFolderNameExists(parentFolder, name, folderFolderRepository)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Folder with the same name already exists");
            }

            Folder childFolder = new Folder(name, parentFolder);
            childFolder = folderRepository.saveAndFlush(childFolder);

            FolderFolderId folderFolderId = new FolderFolderId(parentFolderID, childFolder.getId());
            FolderFolder folderFolder = new FolderFolder(folderFolderId, childFolder.getParent(), childFolder);
            folderFolderRepository.save(folderFolder);

            FileService.createDirectory(FileService.FILES_DIRECTORY + DatabaseFsService.getFolderPath(childFolder.getId(), folderRepository));



            return new ResponseEntity<>(childFolder.getId(), HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }


    @GetMapping
    ResponseEntity<?> getFolders(@RequestParam("id") Integer parentFolderID) {
        try {
            Folder parentFolder = folderRepository.findFolderById(parentFolderID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with id: " + parentFolderID + " not found"));
            List<FolderFolder> parentSubfoldersList = folderFolderRepository.findFolderFoldersByParentFolder(parentFolder);
            List<Folder> subfolders = new ArrayList<>();
            for (FolderFolder folder : parentSubfoldersList) {
                subfolders.add(folder.getChildFolder());
            }
            List<FolderFile> folderFilesList = folderFileRepository.findFolderFilesByFolder(parentFolder);

            List<File> subfiles = new ArrayList<>();
            for (FolderFile file : folderFilesList) {
                subfiles.add(file.getFile());
            }


            return ResponseEntity.ok(new FolderStructure(parentFolder, subfolders, subfiles));

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFolder(@RequestParam("id") Integer folderID) {
        try {
            Folder folder = folderRepository.findFolderById(folderID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with id: " + folderID + " not found"));

            DatabaseFsService.deleteFolder(folderID, folderFolderRepository, folderRepository, folderFileRepository, fileRepository);
            return ResponseEntity.ok(true);


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }



}
