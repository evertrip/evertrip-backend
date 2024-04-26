package com.evertrip.file.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class File {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    private Long size;

    private String path;

    private LocalDateTime createdAt;

    private String fileName;

    public File(Long size, String path, String fileName) {
        this.size = size;
        this.path = path;
        this.fileName = fileName;
        this.createdAt = LocalDateTime.now();
    }

}
