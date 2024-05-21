package com.elephant.server.controllers;

import com.elephant.server.models.File;
import com.elephant.server.models.Folder;
import com.elephant.server.models.FolderFile;
import com.elephant.server.models.FolderFileId;
import com.elephant.server.repositories.FileRepository;
import com.elephant.server.repositories.FolderFileRepository;
import com.elephant.server.repositories.FolderFolderRepository;
import com.elephant.server.repositories.FolderRepository;
import com.elephant.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/fs/file")
public class FileController {
    //TODO probably should do smth to it
    public static final String FILES_DIRECTORY = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "files" + FileSystems.getDefault().getSeparator();
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


            if (isFileExists(parentFolder, name)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "File with the name: " + name + " already exists");
            }
            File newFile = new File(name);
            newFile = fileRepository.saveAndFlush(newFile);

            FolderFileId folderFileId = new FolderFileId(parentID, newFile.getId());

            FolderFile folderFile = new FolderFile(folderFileId, parentFolder, newFile);
            folderFileRepository.saveAndFlush(folderFile);

            fileService.saveFile(file, FILES_DIRECTORY + getPath(parentID), name);

            return new ResponseEntity<>(newFile.getId(), HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }


    }


    private boolean isFileExists(Folder parentFolder, String name) {
        List<FolderFile> parentSubfilesList = folderFileRepository.findFolderFilesByFolder(parentFolder);
        for (FolderFile file : parentSubfilesList) {
            if (file.getFile().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    String getPath(Integer parentID) {
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


