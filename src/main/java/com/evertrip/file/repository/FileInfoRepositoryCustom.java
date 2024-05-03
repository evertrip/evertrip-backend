package com.evertrip.file.repository;

import com.evertrip.file.dto.schedule.DeletedFileInfo;

import java.util.List;

public interface FileInfoRepositoryCustom {

    List<DeletedFileInfo> findDeletedFileInfo();
}
