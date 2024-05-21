package com.elephant.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "folder")
@NoArgsConstructor

public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "folder_id_gen")
    @SequenceGenerator(name = "folder_id_gen", sequenceName = "folder_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @ColumnDefault("nextval('folder_parent_id_seq'::regclass)")
    @JoinColumn(name = "parent_id", nullable = true)
    private Folder parent;

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                '}';
    }

    public Folder(String name, Folder parent) {
        this.name = name;
        this.parent = parent;
    }
}