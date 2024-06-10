package com.evertrip.file.service;

import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.constant.ConstantPool;
import com.evertrip.file.dto.request.FileRequestDto;
import com.evertrip.file.dto.response.FileResponseDto;
import com.evertrip.file.entity.File;
import com.evertrip.file.entity.FileInfo;
import com.evertrip.file.handler.FileHandler;
import com.evertrip.file.repository.FileInfoRepository;
import com.evertrip.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileHandler fileHandler;

    private final FileRepository fileRepository;

    private final FileInfoRepository fileInfoRepository;

    // 파일 저장
    public List<FileResponseDto> uploadFile(List<MultipartFile> multipartFiles) {
        List<File> files = fileHandler.saveAll(multipartFiles);
        fileRepository.saveAll(files);

        return files
                .stream()
                .map(file -> FileResponseDto.response(file, true))
                .collect(Collectors.toList());
    }

    // 파일 1건 삭제 (file_info 테이블 소프트 삭제)
    public void delete(Long fileId) {
        FileInfo fileInfo = findFileInfoByFileId(fileId);
        fileInfo.deleteFileInfo();
    }

    // 파일 목록 삭제 (도메인 삭제시 사용, file_info 테이블 소프트 삭제)
    public void deleteFileList(FileRequestDto dto) {
        List<FileInfo> fileInfos = fileInfoRepository.findFileInfos(dto.getTableName(), dto.getTableKey());
        fileInfos.stream().forEach(fileInfo -> fileInfo.deleteFileInfo());
    }

    // 파일 목록 조회
    public List<FileResponseDto> findFilesByTableInfo(FileRequestDto dto, Boolean isResponse) {
        List<FileResponseDto> files = fileInfoRepository.findFilesByTableInfo(dto.getTableName(), dto.getTableKey());
        if (isResponse) {
            files.forEach(file -> file.setFileName(FileResponseDto.getOriginalFileName(file.getFileName())));
        }
        return files;
    }

    // 파일 정보 저장 (각 도메인의 저장, 수정 시 사용)
    public void saveFileInfo(FileInfo fileInfo) {
        // 파일 정보 존재 시 예외 처리
        if (fileInfoRepository.findFileInfoByFileId(fileInfo.getFile().getId()).isPresent()) {
            throw new ApplicationException(ErrorCode.FILE_INFO_EXISTS);
        }
        fileInfoRepository.save(fileInfo);
    }

    // 파일 정보 리스트 저장 (각 도메인 저장, 수정 시 사용)
    public void saveFileInfoList(List<FileInfo> fileInfoList) {
        fileInfoList.forEach(fileInfo -> {
            // 파일 정보 존재 시 예외 처리
            if (fileInfoRepository.findFileInfoByFileId(fileInfo.getFile().getId()).isPresent()) {
                throw new ApplicationException(ErrorCode.FILE_INFO_EXISTS);
            }
        });
        fileInfoRepository.saveAll(fileInfoList);
    }

    // 파일 조회
    public File findFile(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow(() -> new ApplicationException(ErrorCode.FILE_NOT_FOUND));
    }

    // 파일 id로 파일 정보 조회
    private FileInfo findFileInfoByFileId(Long fileId) {
        return fileInfoRepository.findFileInfoByFileId(fileId).orElseThrow(() -> new ApplicationException(ErrorCode.FILE_NOT_FOUND));
    }

    // 파일 형식 테스트
    public void checkFileExtForProfile(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.lastIndexOf("_"));
        if (!ConstantPool.imageExtList.contains(ext)) {
            throw new ApplicationException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
