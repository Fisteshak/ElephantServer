package com.elephant.server.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FolderStructure {
    Folder parentFolder;
    List<Folder> subfolders;
    List<File> files;
}
