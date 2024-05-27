package com.evertrip.post.specifications;

import com.evertrip.post.entity.Post;
import com.evertrip.tag.entity.PostTags;
import com.evertrip.tag.entity.Tag;
import com.evertrip.tag.repository.TagRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@RequiredArgsConstructor
public class PostSpecifications {

    public static Specification<Post> titleContains(String searchContent) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"), "%" + searchContent + "%");
        };
    }

    public static Specification<Post> tagNameContains(String searchTag) {
        return (root, query, criteriaBuilder) -> {
            // 서브쿼리를 생성합니다.
            Subquery<Long> postTagsSubquery = query.subquery(Long.class);
            Root<PostTags> postTagsRoot = postTagsSubquery.from(PostTags.class);
            Join<PostTags, Tag> tagJoin = postTagsRoot.join("tag");

            // 태그 이름으로 필터링 조건을 생성합니다.
            Predicate tagNamePredicate = criteriaBuilder.equal(tagJoin.get("tagName"), searchTag);

            // 서브쿼리의 select 절을 설정합니다. PostTags에서 Post의 ID를 선택합니다.
            postTagsSubquery.select(postTagsRoot.get("post").get("id"))
                    .where(tagNamePredicate);

            // 메인 쿼리에서 Post의 ID가 서브쿼리의 결과에 포함되는 조건을 설정합니다.
            return criteriaBuilder.in(root.get("id")).value(postTagsSubquery);
        };
    }




    public static Specification<Post> distinct() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.conjunction();
        };
    }
}
