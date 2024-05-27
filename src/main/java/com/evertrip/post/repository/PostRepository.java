package com.evertrip.post.repository;

import com.evertrip.post.dto.request.PostRequestDtoForSearch;
import com.evertrip.post.dto.response.PostResponseDto;
import com.evertrip.post.dto.response.PostResponseForMainDto;
import com.evertrip.post.dto.response.PostResponseForSearchDto;
import com.evertrip.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Query(value = "select new com.evertrip.post.dto.response.PostResponseDto(p.id, p.title, mp.id, mp.nickName, mp.profileImage, p.view, p.likeCount,p.createdAt, p.updatedAt, pd.content ) from Post p " +
            "join PostDetail pd on pd.post.id = p.id and pd.deletedYn = false " +
            "join Member m on m.id = p.member.id and m.deletedYn = false " +
            "join MemberProfile mp on mp.member.id= m.id and mp.deletedYn = false " +
            "where p.id = :postId and p.deletedYn = false")
    Optional<PostResponseDto> getPostDetail(@Param("postId") Long postId);

    @Query(value = "select p from Post p where p.id = :postId and p.deletedYn = false")
    Optional<Post> getPostNotDeleteById(@Param("postId") Long postId);


    @Query("SELECT new com.evertrip.post.dto.response.PostResponseForMainDto(p.id, p.member.id, mp.nickName, p.title, p.createdAt, p.view, p.profileImage, pd.content, p.likeCount) " +
            "FROM Post p " +
            "JOIN MemberProfile mp ON p.member.id = mp.id " +
            "JOIN PostDetail pd ON p.id = pd.post.id " +
            "WHERE p.deletedYn = false " +
            "ORDER BY p.likeCount DESC")
    List<PostResponseForMainDto> findTop30Posts();

    @Query("SELECT new com.evertrip.post.dto.response.PostResponseForMainDto(p.id, p.member.id, mp.nickName, p.title, p.createdAt, p.view, p.profileImage, pd.content, p.likeCount) " +
            "FROM Post p " +
            "JOIN MemberProfile mp ON p.member.id = mp.id " +
            "JOIN PostDetail pd ON p.id = pd.post.id " +
            "WHERE p.deletedYn = false " +
            "ORDER BY p.view DESC")
    List<PostResponseForMainDto> findView30Posts();


//    @Query( "SELECT DISTINCT * FROM (" +
//            "SELECT p.post_id AS id, p.title, mp.memberprifileId , mp.nickname, mp.profile_image, p.view, " +
//            "p.like_count, p.created_at, p.updated_at, pd.content " +
//            "FROM post p " +
//            "JOIN post_detail pd ON p.post_id = pd.post_id " +
//            "JOIN member m ON p.member_id = m.member_id " +
//            "JOIN member_profile mp ON p.member_id = mp.member_id " +
//            "WHERE p.title LIKE CONCAT('%', :searchContent, '%') " +
//            "AND :searchContent IS NOT NULL " +
//            "UNION " +
//            "SELECT p.post_id AS id, p.title, m.member_id, mp.nickname, mp.profile_image, p.view, " +
//            "p.like_count, p.created_at, p.updated_at, pd.content " +
//            "FROM post p " +
//            "JOIN post_detail pd ON p.post_id = pd.post_id " +
//            "JOIN member m ON p.member_id = m.member_id " +
//            "JOIN member_profile mp ON p.member_id = mp.member_id " +
//            "JOIN post_tags pt ON p.post_id = pt.post_id " +
//            "JOIN tag t ON pt.tag_id = t.tag_id " +
//            "WHERE t.tag_name LIKE CONCAT('%', :searchTags, '%') " +
//            "AND :searchTags IS NOT NULL) AS results")
//    List<PostResponseForSearchDto> findPostBySearch(String searchContent, String searchTags);

    Page<Post> findAll(Specification<Post> spec, Pageable pageable);



}
