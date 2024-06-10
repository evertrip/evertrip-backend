package com.evertrip.member.service;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.file.common.BasicImage;
import com.evertrip.file.common.TableName;
import com.evertrip.file.dto.request.FileRequestDto;
import com.evertrip.file.dto.response.FileResponseDto;
import com.evertrip.file.entity.File;
import com.evertrip.file.entity.FileInfo;
import com.evertrip.file.service.FileService;
import com.evertrip.member.dto.request.MemberProfilePatchDto;
import com.evertrip.member.dto.response.MemberProfileResponseDto;
import com.evertrip.member.entity.Member;
import com.evertrip.member.entity.MemberDetail;
import com.evertrip.member.entity.MemberProfile;
import com.evertrip.member.repository.MemberDetailRepository;
import com.evertrip.member.repository.MemberProfileRepository;
import com.evertrip.member.repository.MemberRepository;
import com.evertrip.security.jwt.HmacAndBase64;
import com.evertrip.security.jwt.SymmetricCrypto;
import com.evertrip.security.redis.TokenStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberProfileRepository memberProfileRepository;

    private final MemberRepository memberRepository;

    private final MemberDetailRepository memberDetailRepository;

    private final FileService fileService;

    private final TokenStorageService redisService;

    private final HmacAndBase64 hmacAndBase64;

    private final SymmetricCrypto crypto;

    /**
     * Member pk로 회원 프로필 조회하기
     */
    @Transactional(readOnly = true)
    public List<MemberProfileResponseDto> getMemberProfiles(Long memberId) {
        List<MemberProfileResponseDto> memberProfiles = memberProfileRepository.findMemberProfiles(memberId, false);

        for (MemberProfileResponseDto profile: memberProfiles) {
            String decryptedEmail = crypto.decrypt(profile.getEmail());
            profile.setEmail(decryptedEmail);
        }
        return memberProfiles;
    }


    /**
     * 회원 프로필 수정
     */
    public void updateMemberProfile(Long memberId, MemberProfilePatchDto dto) {
        MemberProfile profile = memberProfileRepository.findByMemberId(memberId,false).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        log.info("MemberProfilePatchDto :" + dto);
        // 프로필 이미지 - fileId 값이 있는 경우
        if (dto.getFileId() != null) {

            // 회원 이미지 세팅
            File file = fileService.findFile(dto.getFileId());
            fileService.checkFileExtForProfile(file.getFileName());

            // 기본이미지 아닐 경우 기존 파일 정보 삭제
            if (!profile.getProfileImage().equals(BasicImage.BASIC_USER_IMAGE.getPath())) {
                FileRequestDto profileImage = FileRequestDto.create(TableName.MEMBER_PROFILE, profile.getId());
                FileResponseDto findProfileFile = fileService.findFilesByTableInfo(profileImage, false).get(0);
                fileService.delete(findProfileFile.getFileId());
            }

            // 공통 : 파일 정보 저장
            fileService.saveFileInfo(new FileInfo(TableName.MEMBER_PROFILE, profile.getId(), file));
            dto.setProfileImage(file.getPath());

        } else if (!StringUtils.hasText(dto.getProfileImage())) {
            // 사진을 제거한 상태이기 때문에 기본 이미지로 세팅해주는 작업
            // 기존 파일 정보 제거
            log.info("BasicImage: {}", BasicImage.BASIC_USER_IMAGE.getPath());
            dto.setProfileImage(BasicImage.BASIC_USER_IMAGE.getPath());
            FileRequestDto profileImage = FileRequestDto.create(TableName.MEMBER_PROFILE, profile.getId());
            List<FileResponseDto> fileList = fileService.findFilesByTableInfo(profileImage, false);

            if (fileList.size() > 0) {
                FileResponseDto findProfileFile = fileList.get(0);
                fileService.delete(findProfileFile.getFileId());
            }

        }

        // 공통 : MemberProfile 수정 작업
        profile.changProfile(dto);

    }

    /**
     * 회원 탈퇴
     */
    public void updateMemberDelete(Long memberId, HttpServletRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        MemberProfile profile = memberProfileRepository.findByMemberId(memberId, false).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        MemberDetail detail = memberDetailRepository.findByMemberId(memberId, false).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        // 회원 삭제
        member.softDelete();
        profile.softDelete();
        detail.softDelete();

        // TODO: 회원이 등록한 게시글 및 게시글 관련 정보 삭제

        // Refresh 토큰을 Redis에서 제거하는 작업
        try {
            redisService.removeRefreshToken("refresh:" + hmacAndBase64.crypt(request.getRemoteAddr(), "HmacSHA512") + "_" + memberId);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ApplicationException(ErrorCode.CRYPT_ERROR);
        }

        // 회원 프로필 이미지 삭제
        fileService.deleteFileList(FileRequestDto.create(TableName.MEMBER_PROFILE, profile.getId()));

        // TODO: 게시글 관련 파일 삭제


    }

}

