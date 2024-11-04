package com.weatherclaus.be.cofig;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${AWS_BUCKET_ACCESSKEY}")
    private String accessKey;

    @Value("${AWS_BUCKET_SECRETKEY}")
    private String secretKey;

    @Value("${AWS_BUCKET_REGION}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {

        // AWS 자격 증명 (Access Key와 Secret Key 설정)
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // AmazonS3 클라이언트 객체 생성 및 반환
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}