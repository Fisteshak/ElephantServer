package com.elephant.server.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    public static final String FILES_DIRECTORY = System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + "files" + FileSystems.getDefault().getSeparator();

    @Async
    public static void saveFile(MultipartFile file, String path, String name) throws IOException {
        Path filePath = createFile(path, name);
        file.transferTo(filePath);
    }

    /**
     * Creates file and all needed directories.
     * @param path
     * @param name
     * @return Path of created file
     * @throws IOException
     */
    public static Path createFile(String path, String name) throws IOException {
        Path directoryPath = Paths.get(path);
        Path filePath = Paths.get(path, name);
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            return filePath;
        } catch (Exception e) {
            //TODO expand error handling
            throw new IOException("Failed to create file at /" + path);
        }
    }

    public static Path createDirectory(String path) throws IOException {
        Path directoryPath = Paths.get(path);
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
        } catch (Exception e) {
            //TODO expand error handling
            throw new IOException("Failed to create directory at /" + path);
        }
        return directoryPath;
    }

    public static void deleteDirectory(String path) throws IOException {
        Path directoryPath = Paths.get(path);
        try {
            if (!Files.exists(directoryPath)) {
                throw new IOException("Entity with path " + directoryPath + " does not exist");
            }
            if (!Files.isDirectory(directoryPath)) {
                throw new IOException("Entity with path " + directoryPath + " is not directory");
            }
            Files.delete(directoryPath);
        } catch (IOException e) {
            //TODO expand error handling
            throw e;
        } catch (Exception e) {
            throw new IOException("Couldn't delete directory with path " + directoryPath);
        }
    }

    public static void deleteFile(String path) throws IOException {
        Path directoryPath = Paths.get(path);
        try {
            if (Files.notExists(directoryPath)) {
                throw new IOException("Entity with path " + directoryPath + " does not exist");
            }
            if (!Files.isRegularFile(directoryPath)) {
                throw new IOException("Entity with path " + directoryPath + " is not file");
            }
            Files.delete(directoryPath);
        } catch (IOException e) {
            //TODO expand error handling
            throw e;
        } catch (Exception e) {
            throw new IOException("Couldn't delete file with path " + directoryPath);
        }
    }


    public static Resource getFile(String path) throws IOException {

        Path file = Paths.get(FILES_DIRECTORY + path);
        Resource resource = new FileSystemResource( file);

        return resource;

    }
}