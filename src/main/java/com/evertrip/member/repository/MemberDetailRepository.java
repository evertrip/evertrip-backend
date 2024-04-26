package com.evertrip.member.repository;

import com.evertrip.member.entity.MemberDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberDetailRepository extends JpaRepository<MemberDetail,Long> {

    @Query("select m from MemberDetail m where m.member.id= :memberId and m.deletedYn = :deletedYn")
    Optional<MemberDetail> findByMemberId(@Param("memberId")Long memberId, @Param("deleteYn") boolean deletedYn);
}
