package com.elephant.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "folder")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "folder_id_gen")
    @SequenceGenerator(name = "folder_id_gen", sequenceName = "folder_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ColumnDefault("nextval('folder_parent_id_seq'::regclass)")
    @JoinColumn(name = "parent_id", nullable = false)
    private Folder parent;

}