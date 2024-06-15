package com.evertrip.file.handler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.evertrip.api.exception.ApplicationException;
import com.evertrip.api.exception.ErrorCode;
import com.evertrip.file.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class
AwsFileHandler implements FileHandler {

    private final String bucket;

    private final AmazonS3 amazonS3Client;

    @Autowired
    public AwsFileHandler(@Value("${cloud.aws.s3.bucket}") String bucket, AmazonS3 amazonS3Client) {
        this.bucket = bucket;
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public File save(MultipartFile file) {
        String newFileName = makeFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            amazonS3Client
                    .putObject(
                            new PutObjectRequest(bucket, newFileName, file.getInputStream(), objectMetadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead)
                    );
        } catch (SdkClientException | IOException e) {
            System.out.println(e);
            throw new ApplicationException(ErrorCode.FILE_STORAGE_ERROR);
        }
        String path = amazonS3Client.getUrl(bucket, newFileName).toString();
        return new File(file.getSize(), path, newFileName);
    }

    @Override
    public void delete(String fileName) {
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            throw new RuntimeException(e);
        }
    }

    private String makeFileName(String originFileName) {
        return new StringBuilder()
                .append(originFileName)
                .append("_")
                .append(UUID.randomUUID())
                .toString();
    }
}
