package com.elephant.server.controllers;

import com.elephant.server.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {
    //TODO probably should do smth to it
    public static final String FILES_DIRECTORY = "D:/Projects/Java/TheElephant/ElephantServer/files/";
    private final FileService fileService = new FileService();

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("name") String name,
                             @RequestParam("path") String path,
                             @RequestParam("file") MultipartFile file)
    {
        try {
            System.out.println(path + name);
            fileService.saveFile(file, FILES_DIRECTORY + path, name);
            //TODO save to DB here, should return ID
            return "uploadSuccess";
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "uploadFailure";

        }
    }

}
