package com.evertrip.file.repository;

import com.evertrip.file.entity.File;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Modifying
    @Query("delete from File f where f.id = :fileId")
    void hardDeleteFile(@Param("fileId") Long fileId);

    @Query("select f from File f where f.id NOT IN (select fi.file.id from FileInfo fi)")
    List<File> findFilesWithNoFileInfo();

}
