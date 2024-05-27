package com.evertrip.file.repository;

import com.evertrip.file.dto.schedule.DeletedFileInfo;
import com.evertrip.file.dto.schedule.DeletedPostContentFile;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RequiredArgsConstructor
public class PostContentFileRepositoryImpl implements PostContentFileCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<DeletedPostContentFile> findDeletedPostContentFile() {
        String sql = "SELECT pcf.post_content_file_id AS id, f.file_id AS fileId " +
                "FROM post_content_file pcf JOIN file f ON pcf.file_id=f.file_id " +
                "WHERE pcf.deleted_at <= DATE_SUB(CONVERT_TZ(now(),'+00:00', '+09:00'), INTERVAL 3 MONTH)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DeletedPostContentFile data = new DeletedPostContentFile(rs.getLong("id"), rs.getLong("fileId"));
            return data;
        });
    }
}
