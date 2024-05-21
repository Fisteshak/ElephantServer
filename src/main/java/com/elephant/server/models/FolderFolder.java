package com.elephant.server.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "folder_folder")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FolderFolder {
    @EmbeddedId
    private FolderFolderId id;

    @MapsId("parentFolderId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "parent_folder_id", nullable = false)
    private Folder parentFolder;

    @MapsId("childFolderId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "child_folder_id", nullable = false)
    private Folder childFolder;


}