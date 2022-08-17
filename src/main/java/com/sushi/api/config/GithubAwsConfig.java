package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"github"})
@Configuration
// @PropertySource("classpath:config/application-github.properties")
public class GithubAwsConfig {

    @Value("${aws.deploy.region:us-west-2}")
    private String targetRegion;

    /**
     * from github secrets
     */
    @Value("${aws.access.key}")
    private String awsAccessKey;

    /**
     * from github secrets
     */
    @Value("${aws.secret.key}")
    private String awsSecretkey;

    /**
     * AWS
     */

    private Regions getTargetRegion() {
        return Regions.fromName(targetRegion);
    }

    @Bean
    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretkey));

        // return DefaultAWSCredentialsProviderChain.getInstance();
    }
    
    
    @Bean
    public AWSSecretsManager awsSecretsManager(AWSCredentialsProvider aWSCredentialsProvider) {
        String endpoint = "secretsmanager." + getTargetRegion().getName() + ".amazonaws.com";
        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, getTargetRegion().getName());
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(config);
        clientBuilder.setCredentials(aWSCredentialsProvider);
        return clientBuilder.build();
    }
}
