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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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

    @Query(value = "select p.view from Post p where p.id = :postId and p.deletedYn = false")
    Optional<Long> getViews(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query(value = "update Post p set p.view = :view where p.id = :postId")
    void updateView(@Param("postId") Long postId, @Param("view") Long view);





    @Query("SELECT new com.evertrip.post.dto.response.PostResponseForMainDto(p.id, p.member.id, mp.nickName, p.title, p.createdAt, p.view, p.profileImage, pd.content, p.likeCount) " +
            "FROM Post p " +
            "JOIN MemberProfile mp ON p.member.id = mp.id " +
            "JOIN PostDetail pd ON p.id = pd.post.id " +
            "WHERE p.deletedYn = false " +
            "ORDER BY p.likeCount DESC")
    List<PostResponseForMainDto> findTop30Posts(Pageable pageable);

    @Query("SELECT new com.evertrip.post.dto.response.PostResponseForMainDto(p.id, p.member.id, mp.nickName, p.title, p.createdAt, p.view, p.profileImage, pd.content, p.likeCount) " +
            "FROM Post p " +
            "JOIN MemberProfile mp ON p.member.id = mp.id " +
            "JOIN PostDetail pd ON p.id = pd.post.id " +
            "WHERE p.deletedYn = false " +
            "ORDER BY p.view DESC" )
    List<PostResponseForMainDto> findView30Posts(Pageable pageable);



    Page<Post> findAll(Specification<Post> spec, Pageable pageable);

    @Query ("select p from Post p where p.deletedYn = false order by p.createdAt")
    List<Post> findAllByPage(Pageable pageable);
}
