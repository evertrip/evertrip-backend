package com.evertrip.file.entity;

import com.evertrip.file.common.TableName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class FileInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_info_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TableName tableName;

    private Long tableKey;

    @OneToOne
    @JoinColumn(name="file_id")
    private File file;

    private LocalDateTime deletedAt;

    public FileInfo(TableName tableName, Long tableKey, File file) {
        this.tableName = tableName;
        this.tableKey = tableKey;
        this.file = file;
    }

    public static List<FileInfo> createFileInfos(List<File> files, TableName tableName, Long tableKey) {
        return files
                .stream()
                .map(file -> new FileInfo(tableName, tableKey, file))
                .collect(Collectors.toList());
    }

    public void deleteFileInfo() {
        this.deletedAt = LocalDateTime.now();
    }
}
