package com.elephant.server.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class FolderFileId implements java.io.Serializable {
    private static final long serialVersionUID = 4471166429757289076L;
    @Column(name = "folder_id", nullable = false)
    private Integer folderId;

    @Column(name = "file_id", nullable = false)
    private Integer fileId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FolderFileId entity = (FolderFileId) o;
        return Objects.equals(this.folderId, entity.folderId) &&
                Objects.equals(this.fileId, entity.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folderId, fileId);
    }

}