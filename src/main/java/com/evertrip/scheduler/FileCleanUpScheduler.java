package com.evertrip.scheduler;

import com.evertrip.file.dto.schedule.DeletedFileInfo;
import com.evertrip.file.entity.File;
import com.evertrip.file.handler.FileHandler;
import com.evertrip.file.repository.FileInfoRepository;
import com.evertrip.file.repository.FileRepository;
import com.evertrip.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileCleanUpScheduler {

    private final FileService fileService;

    private final FileInfoRepository fileInfoRepository;

    private final FileRepository fileRepository;

    private final FileHandler fileHandler;



    /**
     * 소프트 삭제된 지 3개월이 지난 파일 정보와 그에 해당하는 파일 테이블, S3 파일들을 스케줄링한다
     * 매일 자정에 스케줄링이 동작하게 된다
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanUpExpiredFileInfosData() {
        // 삭제해야할 파일 정보 찾기
        // Mysql RDS 인스턴스 타임존 설정을 UTC로 해서 쿼리상으로 KST 시간으로 변환해줬다
        log.info("소프트 삭제 후 3개월 지난 파일 정보 및 관련 데이터 정기 삭제 스케줄러 동작");
        List<DeletedFileInfo> deletedFileInfos = fileInfoRepository.findDeletedFileInfo();
        log.info("삭제 대상 파일 Info size: " + deletedFileInfos.size());
        // TODO: 성능 개선을 위해 Spring Batch 혹은 벌크 연산을 이용하는 것이 좋아보인다
        for (DeletedFileInfo fileInfo: deletedFileInfos) {
            File findFile = fileService.findFile(fileInfo.getFileId());

            // 파일, 파일 정보 테이블 delete 로직
            fileInfoRepository.hardDeleteFileInfo(fileInfo.getId());
            fileRepository.hardDeleteFile(findFile.getId());

            // S3 저장소에 파일 삭제 로직
            fileHandler.delete(findFile.getFileName());
        }



    }

    /**
     * 파일 정보에 속하지 않은 파일 테이블의 데이터를 스케줄링한다
     * 매일 자정에 스케줄링이 동작하게 된다
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanUpUnmatchedFile() {
        // 삭제해야할 파일 찾기
        log.info("파일 정기 삭제 스케줄러 동작");
        List<File> filesToCleanUp = fileRepository.findFilesWithNoFileInfo();
        log.info("정기 삭제 대상 파일 size : " + filesToCleanUp.size());
        // TODO: 성능 개선을 위해 Spring Batch 혹은 벌크 연산을 이용하는 것이 좋아보인다
        for (File file: filesToCleanUp) {
            // 파일 테이블 delete 로직
            fileRepository.hardDeleteFile(file.getId());
            // S3 저장소에 파일 삭제 로직
            fileHandler.delete(file.getFileName());
        }


    }

}
