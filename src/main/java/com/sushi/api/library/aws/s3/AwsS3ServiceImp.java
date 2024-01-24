package com.sushi.api.library.aws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.sushi.api.exception.ApiError;
import com.sushi.api.exception.ApiException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.Date;

@Setter
@Service
@Slf4j
public class AwsS3ServiceImp implements AwsS3Service {

    @Value("${aws.s3.bucket}")
    private String S3_BUCKET;
    
    @Value("${spring.application.name}")
    private String appName;

    @Value("${aws.deploy.region:us-west-2}")
    private String awsRegion;
    
    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public AwsUploadResponse uploadPublicObj(String objectKey, ObjectMetadata metadata, InputStream inputStream) {
        return uploadPublicObj(S3_BUCKET, objectKey, metadata, inputStream);
    }

    @Override
    public AwsUploadResponse uploadPublicObj(String s3Bucket, String objectKey, ObjectMetadata metadata, InputStream inputStream) {

        PutObjectResult result = null;
        
        String s3Key = appName + "/" + env + "/" + objectKey;

        try {
            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(s3Bucket, s3Key, inputStream, metadata);

            result = amazonS3.putObject(request);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            return null;
        }

        URL objectUrl = amazonS3.getUrl(s3Bucket, s3Key);

        String generatedUrl = generateUrl(objectKey);

        return new AwsUploadResponse(objectKey, objectUrl.toString(), generatedUrl);
    }


    @Override
    public AwsUploadResponse uploadPrivateObj(String objectKey, ObjectMetadata metadata, InputStream inputStream) {

        PutObjectResult result = null;
        
        String s3Key = appName + "/" + env + "/" + objectKey;

        try {
            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(S3_BUCKET, s3Key, inputStream, metadata);

            result = amazonS3.putObject(request);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            return null;
        }

        URL objectUrl = amazonS3.getUrl(S3_BUCKET, s3Key);

        String generatedUrl = generateUrl(objectKey);

        return new AwsUploadResponse(objectKey, objectUrl.toString(), generatedUrl);
    }

    @Override
    public AwsUploadResponse refreshTTL(String objectKey) {
        // URL objectUrl = amazonS3.getUrl(S3_BUCKET, s3key);

        URL url = amazonS3.generatePresignedUrl(S3_BUCKET, objectKey, DateUtils.addHours(new Date(), 6));

        String generatedUrl = generateUrl(objectKey);

        return new AwsUploadResponse(objectKey, url.toString(), generatedUrl);
    }

    @Override
    public S3PresignedUrlDTO generatePresignedUrl(String awsS3Key) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5 minutes
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = null;

        try {
            generatePresignedUrlRequest = new GeneratePresignedUrlRequest(S3_BUCKET, awsS3Key).withMethod(HttpMethod.PUT).withExpiration(expiration);

        } catch (Exception e) {
            log.warn("Exception with GeneratePresignedUrlRequest, msg={}", e.getLocalizedMessage());
            throw new ApiException(ApiError.DEFAULT_MSG, "Error with GeneratePresignedUrlRequest, msg=" + e.getLocalizedMessage());
        }

        S3PresignedUrlDTO s3PresignedUrlDTO = new S3PresignedUrlDTO();

        s3PresignedUrlDTO.setExpiration(expiration);

        try {
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            s3PresignedUrlDTO.setPresignedUrl(url.toString());
        } catch (Exception e) {
            log.warn("Exception with generatePresignedUrl, msg={}", e.getLocalizedMessage());
            throw new ApiException(ApiError.DEFAULT_MSG, "Error with generatePresignedUrl, msg=" + e.getLocalizedMessage());
        }

        return s3PresignedUrlDTO;
    }

    private String generateUrl(String key) {

        String objectUrl = String.format("https://s3.%s.amazonaws.com/%s/%s", awsRegion, S3_BUCKET, key);

        return objectUrl;

    }

    @Override
    public File downloadFile(String key) {
        return downloadFile(S3_BUCKET, key);
    }

    @Override
    public File downloadFile(String s3Bucket, String key) {

        File file = null;
        try {
            S3Object s3object = amazonS3.getObject(new GetObjectRequest(s3Bucket, key));
            file = new File(key);
            s3object.getObjectContent().transferTo(new FileOutputStream(file));
        } catch (IOException e) {
            log.warn("IOException, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.warn("Exception, msg={}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return file;
    }


}
