package com.elephant.server.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "folder_file")
@AllArgsConstructor
@NoArgsConstructor
public class FolderFile {
    @EmbeddedId
    private FolderFileId id;

    @MapsId("folderId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @MapsId("fileId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

}