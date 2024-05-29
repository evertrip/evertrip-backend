package com.evertrip.file.repository;

import com.evertrip.file.dto.schedule.DeletedFileInfo;
import com.evertrip.file.dto.schedule.DeletedPostContentFile;
import com.evertrip.file.entity.PostContentFile;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    @Override
    public void batchInsertPostContentFiles(List<PostContentFile> postContentFiles) {
        String sql = "INSERT INTO post_content_file "
                + "(post_id, file_id, deleted_at) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostContentFile postContentFile = postContentFiles.get(i);
                ps.setLong(1,postContentFile.getPostId());
                ps.setLong(2,postContentFile.getFileId());
                ps.setObject(3, postContentFile.getDeletedAt());
            }

            @Override
            public int getBatchSize() {
                return postContentFiles.size();
            }
        });
    }

}
