package com.sushi.api.library.aws.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.io.InputStream;

public interface AwsS3Service {

    AwsUploadResponse uploadPublicObj(String objectKey, ObjectMetadata metadata, InputStream speechStream);

    AwsUploadResponse uploadToRootFolder(String objectKey, ObjectMetadata metadata, InputStream speechStream);

//    AwsUploadResponse uploadPrivateObj(String objectKey, ObjectMetadata metadata, InputStream speechStream);

    public File downloadFile(String s3Bucket, String key);

    public File downloadFile(String key);

    AwsUploadResponse refreshTTL(String s3key);

    S3PresignedUrlDTO generatePresignedUrl(String awsS3Key);
}
