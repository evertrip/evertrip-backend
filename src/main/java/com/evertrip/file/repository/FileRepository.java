package com.evertrip.file.repository;

import com.evertrip.file.entity.File;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Modifying
    @Query("delete from File f where f.id = :fileId")
    void hardDeleteFile(@Param("fileId") Long fileId);

    @Modifying
    @Query(value = "DELETE FROM file WHERE file_id = :fileId AND created_at <= DATE_SUB(CONVERT_TZ(now(), '+00:00', '+09:00'), INTERVAL 3 MONTH)", nativeQuery = true)
    void hardDeleteFileExpired(@Param("fileId") Long fileId);

    @Query("select f from File f where f.id NOT IN (select fi.file.id from FileInfo fi) and f.id NOT IN (select pcf.fileId from PostContentFile pcf)")
    List<File> findFilesUnrelated();

}
