package com.elephant.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "folder_folder")
public class FolderFolder {
    @EmbeddedId
    private FolderFolderId id;

    @MapsId("parentFolderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_folder_id", nullable = false)
    private Folder parentFolder;

    @MapsId("childFolderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_folder_id", nullable = false)
    private Folder childFolder;

}