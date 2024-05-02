package com.evertrip.file.repository;

import com.evertrip.file.common.TableName;
import com.evertrip.file.dto.response.FileResponseDto;
import com.evertrip.file.entity.FileInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long>, FileInfoRepositoryCustom {

    @Query("select f from FileInfo f where f.file.id = :fileId and f.deletedAt is null")
    Optional<FileInfo> findFileInfoByFileId(@Param("fileId") Long fileId);

    @Query("select fi from FileInfo fi where fi.tableName = :tableName and fi.tableKey = :tableKey and fi.deletedAt is null")
    List<FileInfo> findFileInfos(@Param("tableName") TableName tableName, @Param("tableKey") Long tableKey);

    @Query("select new com.evertrip.file.dto.response.FileResponseDto(f.id,f.size,f.path,f.fileName,f.createdAt,fi.id) from FileInfo fi " +
            "join fi.file f where fi.tableName = :tableName and fi.tableKey =:tableKey and fi.deletedAt is null")
    List<FileResponseDto> findFilesByTableInfo(@Param("tableName") TableName tableName, @Param("tableKey") Long tableKey);

    @Modifying
    @Query("delete from FileInfo fi where fi.id = :fileInfoId")
    void hardDeleteFileInfo(@Param("fileInfoId") Long fileInfoId);

}
