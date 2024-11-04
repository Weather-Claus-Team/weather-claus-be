package com.weatherclaus.be.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    @Value("${AWS_BUCKET_NAME}")
    private String bucketName;

    private final AmazonS3 amazonS3;


    // S3에 파일 업로드
    public String uploadFile(MultipartFile file) throws IOException {

        // 파일 고유 이름 생성
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // 파일 S3에 업로드
        amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);

        String imageUrl = amazonS3.getUrl(bucketName, fileName).toString();

        // S3에 저장된 파일의 URL 반환
        return imageUrl;
    }


    // S3에 있는 파일 삭제
    public void deleteFile(String imageUrl) throws UnsupportedEncodingException {
        // URL에서 파일 이름 추출
        String fileName = extractFileNameFromUrl(imageUrl);

            amazonS3.deleteObject(bucketName, fileName);
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            log.info("File deleted successfully");

    }

    // URL에서 파일 이름 추출하는 메서드
    private String extractFileNameFromUrl(String imageUrl) throws UnsupportedEncodingException {
        return URLDecoder.decode(imageUrl.substring(imageUrl.lastIndexOf("/") + 1), "UTF-8");
    }


}