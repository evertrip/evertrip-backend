package com.evertrip.file.handler;

import com.amazonaws.services.s3.AmazonS3;
import com.evertrip.file.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AwsFileHandler implements FileHandler {

    private final String bucket;

    private final AmazonS3 amazonS3Client;

    @Autowired
    public AwsFileHandler(@Value("${cloud.aws.s3.bucket}") String bucket, AmazonS3 amazonS3Client) {
        this.bucket = bucket;
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public File save(MultipartFile file) {
        return null;
    }

    @Override
    public void delete(String fileName) {

    }
}
