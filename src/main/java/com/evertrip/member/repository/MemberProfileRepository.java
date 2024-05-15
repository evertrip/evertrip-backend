package com.evertrip.member.repository;

import com.evertrip.member.dto.response.MemberProfileResponseDto;
import com.evertrip.member.entity.MemberProfile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile,Long> {


    @Query("select new com.evertrip.member.dto.response.MemberProfileResponseDto(mp.member.id, mp.nickName, mp.description, mp.createdAt, mp.updatedAt,mp.profileImage,m.email) " +
            "from MemberProfile mp " +
            "join Member m on m.id= :memberId and m.deletedYn = :deletedYn " +
            "where mp.member.id= :memberId and mp.deletedYn = :deletedYn")
    List<MemberProfileResponseDto> findMemberProfiles(@Param("memberId") Long memberId, @Param("deletedYn") boolean deletedYn);

    @Query("select m from MemberProfile m where m.member.id= :memberId and m.deletedYn = :deletedYn")
    Optional<MemberProfile> findByMemberId(@Param("memberId")Long memberId, @Param("deleteYn") boolean deletedYn);
}
