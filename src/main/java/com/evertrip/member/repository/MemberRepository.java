package com.evertrip.member.repository;

import com.evertrip.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    @Query(value = "select m from Member m where m.email = :email and m.deletedYn=false")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select m from Member m where m.id = :memberId and m.deletedYn=false")
    Optional<Member> findByIdNotDeleted(@Param("memberId") Long memberId);
}
