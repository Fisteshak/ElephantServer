package com.elephant.server.controllers;

import com.elephant.server.models.File;
import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFile;
import com.elephant.server.models.FolderFileId;
import com.elephant.server.repositories.FileRepository;
import com.elephant.server.repositories.FolderFileRepository;
import com.elephant.server.repositories.FolderFolderRepository;
import com.elephant.server.repositories.FolderRepository;
import com.elephant.server.service.DatabaseFsService;
import com.elephant.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.FileSystems;

@RestController
@RequestMapping("/fs/file")
public class FileController {
    //TODO probably should do smth to it
    private final FileService fileService = new FileService();

    @Autowired
    FileRepository fileRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    FolderFileRepository folderFileRepository;
    @Autowired
    FolderFolderRepository folderFolderRepository;



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("name") String name,
                                        @RequestParam("parent_id") Integer parentID,
                                        @RequestParam("file") MultipartFile file)
    {
        try {
            Folder parentFolder = folderRepository.findFolderById(parentID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with id: " + parentID + " not found"));

            if (DatabaseFsService.isFileExists(parentFolder, name, folderFileRepository)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "File with the name: " + name + " already exists");
            }
            File newFile = new File(name, parentFolder);
            newFile = fileRepository.saveAndFlush(newFile);

            FolderFileId folderFileId = new FolderFileId(parentID, newFile.getId());

            FolderFile folderFile = new FolderFile(folderFileId, parentFolder, newFile);
            folderFileRepository.saveAndFlush(folderFile);

            FileService.saveFile(file, FileService.FILES_DIRECTORY + DatabaseFsService.getFolderPath(parentID, folderRepository), name);

            return new ResponseEntity<>(newFile.getId(), HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getFile(@RequestParam("file_id") Integer file_id) {
        try {
            File file = fileRepository.findFileById(file_id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File with id: " + file_id + " not found"));

            Folder parentFolder = file.getParent();

//            Folder parentFolder = folderFileRepository.findFolderFileByFileId(file_id)
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder with file id: " + file_id + " not found"))
//                    .getFolder();

            String filepath = DatabaseFsService.getFolderPath(parentFolder.getId(), folderRepository) + FileSystems.getDefault().getSeparator() + file.getName();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(FileService.getFile(filepath));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }

    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam("id") Integer id) {
        try {
            DatabaseFsService.deleteFile(id, folderFileRepository, fileRepository);
            return ResponseEntity.ok(true);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }





}


