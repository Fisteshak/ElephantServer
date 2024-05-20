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
public class FolderFolderId implements java.io.Serializable {
    private static final long serialVersionUID = -1363561135963532959L;
    @Column(name = "parent_folder_id", nullable = false)
    private Integer parentFolderId;

    @Column(name = "child_folder_id", nullable = false)
    private Integer childFolderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FolderFolderId entity = (FolderFolderId) o;
        return Objects.equals(this.parentFolderId, entity.parentFolderId) &&
                Objects.equals(this.childFolderId, entity.childFolderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFolderId, childFolderId);
    }

}