//package com.ruoyi.common.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class CustomUploadClient {
//
//    private final UploadProperties uploadProperties;
//
////    @Bean
////    public MinioClient minioClient() {
////        return MinioClient.builder()
////                .endpoint(uploadProperties.getEndpoint())
////                .region(uploadProperties.getRegion())
////                .credentials(uploadProperties.getAccessKey(),uploadProperties.getSecretKey())
////                .build();
////    }
//
//    @Bean
//    public AmazonS3 amazonS3Client() {
//
//        return AmazonS3Client.builder()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(uploadProperties.getEndpoint(), uploadProperties.getRegion()))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(uploadProperties.getAccessKey(), uploadProperties.getSecretKey())))
//                .build();
//    }
//}
