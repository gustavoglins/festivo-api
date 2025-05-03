package com.festivo.domain.services.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.festivo.domain.services.interfaces.AwsS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public AwsS3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadFile(MultipartFile file) throws SdkClientException, IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        return getFileUrl(fileName);
    }

    @Override
    public Resource downloadFile(String fileName) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        var bytes = s3Object.getObjectContent().readAllBytes();
        return new ByteArrayResource(bytes);
    }

    @Override
    public String getFileUrl(String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }

}