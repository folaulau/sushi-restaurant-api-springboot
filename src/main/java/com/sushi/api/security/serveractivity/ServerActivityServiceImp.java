package com.sushi.api.security.serveractivity;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushi.api.library.aws.s3.AwsS3Service;
import com.sushi.api.library.aws.s3.AwsUploadResponse;
import com.sushi.api.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ServerActivityServiceImp implements ServerActivityService{

    @Autowired
    private ServerActivityDAO serverActivityDAO;

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private Environment environment;

    @Async
    @Override
    public void addApplicationStartUp() {
        ServerActivity serverActivity = new ServerActivity(null, "server-startup", "localhost", LocalDateTime.now());

        serverActivity = serverActivityDAO.save(serverActivity);

        this.addActivityToS3(serverActivity);
    }

    @Override
    public void addActivityToS3(ServerActivity serverActivity) {
        String env = List.of(environment.getActiveProfiles()).stream().map(profile -> profile.toLowerCase())
                .findFirst().orElse("local");

        String objectKey = "db_last_activity_"+env+".json";

        File jsonFile = awsS3Service.downloadFile(objectKey);

        try {

            ObjectMapper mapper = ObjectUtils.getObjectMapper();

            Map<String, Object> data = null;

            if(jsonFile == null){
                data = new HashMap<>();
            }else{
                data = mapper.readValue(jsonFile, new TypeReference<HashMap<String, Object>>(){});
            }

            data.put("createdAt", serverActivity.getCreatedAt());
            data.put("latestApp", "sushi");
            data.put("sushi", serverActivity);
            
            String json = mapper.writeValueAsString(data);


            // Convert JSON string to InputStream
            InputStream inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));

            // Set up metadata (specify content type and encoding if needed)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("application/json");
            metadata.setContentEncoding("UTF-8");
            metadata.setContentLength(json.length());

            // The object key here could be something like "server_activity/yyyy-MM-dd/uuid.json"
            // You can use serverActivity.getId() or the current date to make it unique


            // Use your service to upload the InputStream as a public or private object
            AwsUploadResponse response = awsS3Service.uploadPublicObj(objectKey, metadata, inputStream);

            if (response != null) {
                // Success - Handle response
//                System.out.println("Uploaded ServerActivity to S3: " + response.getObjectUrl());
            } else {
                // Failure - Handle accordingly
                System.out.println("Failed to upload ServerActivity to S3");
            }

        } catch (Exception e) {
            log.warn("Exception, msg={}",e.getLocalizedMessage());
        }

    }
}
