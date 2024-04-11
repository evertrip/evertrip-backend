package com.evertrip.file.dto.response;

import com.evertrip.file.entity.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {

    private Long fileId;

    private Long size;

    private String path;

    private String fileName;

    private String createdAt;

    private Long fileInfoId;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileResponseDto(Long fileId, Long size, String path, String fileName, LocalDateTime createdAt, Long fileInfoId) {
        this.fileId = fileId;
        this.size = size;
        this.path = path;
        this.fileName = fileName;
        this.createdAt = createdAt.toString().replace("T", " ");
        this.fileInfoId = fileInfoId;
    }

    public static FileResponseDto response(File file, Boolean isResponse) {
        FileResponseDto responseDto = FileResponseDto.builder()
                .fileId(file.getId())
                .path(file.getPath())
                .size(file.getSize())
                .fileName(file.getFileName())
                .createdAt(file.getCreatedAt().toString().replace("T", " "))
                .build();

        if (isResponse) {
            responseDto.setFileName(getOriginalFileName(responseDto.getFileName()));
        }

        return responseDto;
    }

    public static String getOriginalFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("_"));
    }
}
