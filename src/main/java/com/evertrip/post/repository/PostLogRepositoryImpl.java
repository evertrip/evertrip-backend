package com.evertrip.post.repository;

import com.evertrip.constant.ConstantPool;
import com.evertrip.post.dto.response.postlog.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class PostLogRepositoryImpl implements PostLogRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PostLogVisitorsDto> getNumberOfVisitors(Long postId) {
        // 방문자 중복 없앤 쿼리
//        String sql = "SELECT COUNT(DISTINCT member_id) AS visitors, 'total' AS period " +
//                "FROM post_log WHERE event_type = 'VIEWER' AND post_id = " + postId + " " +
//                "UNION ALL " +
//                "SELECT COUNT(DISTINCT member_id) AS visitors, DATE_FORMAT(created_at, '%Y%m') AS period " +
//                "FROM post_log WHERE event_type = 'VIEWER' AND post_id = " + postId + " " +
//                "AND created_at BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 YEAR) AND CURDATE() " +
//                "GROUP BY DATE_FORMAT(created_at, '%Y%m') ORDER BY period";

        // 방문자 중복 허용 쿼리
        String sql = "SELECT COUNT(member_id) AS visitors, 'total' AS period " +
                "FROM post_log WHERE event_type = 'VIEWER' AND post_id = " + postId + " AND deleted_yn = false " +
                "UNION ALL " +
                "SELECT COUNT(member_id) AS visitors, DATE_FORMAT(created_at, '%Y%m') AS period " +
                "FROM post_log WHERE event_type = 'VIEWER' AND post_id = " + postId + " AND deleted_yn = false " +
                "AND created_at BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 YEAR) AND CURDATE() " +
                "GROUP BY DATE_FORMAT(created_at, '%Y%m') ORDER BY period";

        return jdbcTemplate.query(sql, new RowMapper<PostLogVisitorsDto>() {
            @Override
            public PostLogVisitorsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                PostLogVisitorsDto dto = new PostLogVisitorsDto(rs.getLong("visitors"),rs.getString("period"));
                return dto;
            }
        });
    }

    @Override
    public List<PostLogVisitorsHistoryDto> getVisitorsHistory(Long postId) {
        String sql = "SELECT p.member_id AS memberId, mp.nickname AS nickName, mp.profile_image AS profileImage, p.created_at AS eventCreatedAt " +
                     "FROM post_log p " +
                     "JOIN member_profile mp ON mp.member_id = p.member_id "+
                     "WHERE p.event_type = 'VIEWER' AND p.post_id = " + postId + " AND p.deleted_yn = false " +
                     "ORDER BY eventCreatedAt DESC  LIMIT 30";

        return jdbcTemplate.query(sql, new RowMapper<PostLogVisitorsHistoryDto>() {
            @Override
            public PostLogVisitorsHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                PostLogVisitorsHistoryDto dto = new PostLogVisitorsHistoryDto(rs.getLong("memberId"),rs.getString("nickName"),
                        rs.getString("profileImage"),rs.getTimestamp("eventCreatedAt").toLocalDateTime());
                return dto;
            }
        });
    }

    @Override
    public List<PostLogScrollDto> getVisitorsScroll(Long postId) {
        String sql = "SELECT AVG(event_content) AS scroll, 'total' AS period " +
                "FROM post_log WHERE event_type = 'SCROLL' AND post_id = " + postId + " AND deleted_yn = false " +
                "UNION ALL " +
                "SELECT AVG(event_content) AS scroll, DATE_FORMAT(created_at, '%Y%m') AS period " +
                "FROM post_log WHERE event_type = 'SCROLL' AND post_id = " + postId + " AND deleted_yn = false " +
                "AND created_at BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 YEAR) AND CURDATE() " +
                "GROUP BY DATE_FORMAT(created_at, '%Y%m') ORDER BY period";

        return jdbcTemplate.query(sql, new RowMapper<PostLogScrollDto>() {
            @Override
            public PostLogScrollDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                PostLogScrollDto dto = new PostLogScrollDto(rs.getInt("scroll"),rs.getString("period"));
                return dto;
            }
        });

    }

    @Override
    public List<PostLogStayingDto> getVisitorsStaying(Long postId) {
        String sql = "SELECT AVG(event_content) AS staying, 'total' AS period " +
                "FROM post_log WHERE event_type = 'STAYING' AND post_id = " + postId + " AND deleted_yn = false " +
                "UNION ALL " +
                "SELECT AVG(event_content) AS staying, DATE_FORMAT(created_at, '%Y%m') AS period " +
                "FROM post_log WHERE event_type = 'STAYING' AND post_id = " + postId + " AND deleted_yn = false " +
                "AND created_at BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 YEAR) AND CURDATE() " +
                "GROUP BY DATE_FORMAT(created_at, '%Y%m') ORDER BY period";

        return jdbcTemplate.query(sql, new RowMapper<PostLogStayingDto>() {
            @Override
            public PostLogStayingDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                PostLogStayingDto dto = new PostLogStayingDto(rs.getInt("staying"),rs.getString("period"));
                return dto;
            }
        });
    }

    @Override
    public List<PostLogHistoryDto> getHistory(Long postId) {
        String sql = "SELECT p.member_id AS memberId, mp.nickname AS nickName, mp.profile_image AS profileImage," +
                "p.event_type AS eventType, p.event_content AS eventContent ,p.created_at AS eventCreatedAt " +
                "FROM post_log p " +
                "JOIN member_profile mp ON mp.member_id = p.member_id "+
                "WHERE p.event_type = 'HISTORY' AND p.post_id = " + postId + " AND p.deleted_yn = false " +
                "ORDER BY eventCreatedAt DESC";

        return jdbcTemplate.query(sql, new RowMapper<PostLogHistoryDto>() {
            @Override
            public PostLogHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                PostLogHistoryDto dto = new PostLogHistoryDto(rs.getLong("memberId"),rs.getString("nickName")
                        ,rs.getString("profileImage"),  ConstantPool.EventType.valueOf(rs.getString("eventType")),rs.getString("eventContent"),rs.getTimestamp("eventCreatedAt").toLocalDateTime());
                return dto;
            }
        });
    }
}
