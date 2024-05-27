package com.evertrip.file.repository;

import com.evertrip.file.dto.schedule.DeletedPostContentFile;

import java.util.List;

public interface PostContentFileCustom {

    List<DeletedPostContentFile> findDeletedPostContentFile();
}
