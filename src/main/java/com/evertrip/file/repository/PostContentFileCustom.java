package com.evertrip.file.repository;

import com.evertrip.file.dto.schedule.DeletedPostContentFile;
import com.evertrip.file.entity.PostContentFile;

import java.util.List;

public interface PostContentFileCustom {

    List<DeletedPostContentFile> findDeletedPostContentFile();

    void batchInsertPostContentFiles(List<PostContentFile> postContentFiles);
}
