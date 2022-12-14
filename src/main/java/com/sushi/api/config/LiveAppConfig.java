package com.sushi.api.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.sendgrid.SendGrid;
import com.sushi.api.library.aws.secretsmanager.AwsSecretsManagerService;
import com.sushi.api.library.aws.secretsmanager.DatabaseSecrets;
import com.sushi.api.library.aws.secretsmanager.FirebaseSecrets;
import com.sushi.api.library.aws.secretsmanager.SMTPSecrets;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import com.sushi.api.library.aws.secretsmanager.TwilioSecrets;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.sushi.api.utils.ObjectUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"prod"})
@Configuration
public class LiveAppConfig {

  @Value("${aws.deploy.region:us-west-2}")
  private String targetRegion;

  @Value("${spring.datasource.name}")
  private String databaseName;

  @Autowired
  private AwsSecretsManagerService awsSecretsManagerService;

  /* ================== datasource =============== */
  @Bean
  public HikariDataSource dataSource() {
    log.info("Configuring dataSource...");

    DatabaseSecrets databaseSecrets = awsSecretsManagerService.getDbSecret();

    log.info("databaseSecrets={}", ObjectUtils.toJson(databaseSecrets));
    // jdbc:postgresql://localhost:5432/learnmymath_api_db
    int port = 5432;
    String host = databaseSecrets.getHost();
    String username = databaseSecrets.getUsername();
    String password = databaseSecrets.getPassword();
    String url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");

    HikariDataSource hds = new HikariDataSource(config);
    hds.setMaximumPoolSize(30);
    hds.setMinimumIdle(5);
    hds.setMaxLifetime(1800000);
    hds.setConnectionTimeout(30000);
    hds.setIdleTimeout(600000);
    // 45 seconds
    hds.setLeakDetectionThreshold(45000);

    log.info("DataSource configured!");

    return hds;
  }

  @Bean(name = "stripeSecrets")
  public StripeSecrets stripeSecrets() {
    return awsSecretsManagerService.getStripeSecrets();
  }

//  @Bean(name = "twilioSecrets")
//  public TwilioSecrets twilioSecrets() {
//    return awsSecretsManagerService.getTwilioSecrets();
//  }

  @Bean(name = "queue")
  public String queue(@Value("${queue}") String queue) {
    return queue;
  }

  @Bean(name = "xApiKey")
  public XApiKey xApiKeySecrets() {
    return awsSecretsManagerService.getXApiKeys();
  }
  
  @Bean(name = "firebaseSecrets")
  public FirebaseSecrets firebaseSecrets() {
    FirebaseSecrets firebaseSecrets = awsSecretsManagerService.getFirebaseSecrets();
    return firebaseSecrets;
  }

//  @Bean
//  public SendGrid sendGrid() {
//    SMTPSecrets sMTPSecrets = awsSecretsManagerService.getSMTPSecrets();
//    SendGrid sendGrid = new SendGrid(sMTPSecrets.getPassword());
//    return sendGrid;
//  }

  /**
   * AWS
   */

  private Regions getTargetRegion() {
    return Regions.fromName(targetRegion);
  }

  @Bean
  public FirebaseApp firebaseApp() {
    FirebaseApp firebaseApp = null;
    try {
      if (FirebaseApp.getInstance() != null) {
        firebaseApp = FirebaseApp.getInstance();
        log.info("MyFirebase config had already been set up, name: " + firebaseApp.getName());
        return firebaseApp;
      } else {
        log.info("MyFirebase config is null. Configuring one...");
      }
    } catch (Exception e) {
      log.warn("FirebaseApp Exception, msg: " + e.getLocalizedMessage());
    }

    FirebaseSecrets firebaseSecrets = awsSecretsManagerService.getFirebaseSecrets();
    
//    log.info("firebaseSecrets={}", firebaseSecrets.toJson());

    try {
          // @formatter:off
          InputStream is = new ByteArrayInputStream(firebaseSecrets.getAdminFileContent().getBytes());
          
          FirebaseOptions options = FirebaseOptions.builder()
                  .setCredentials(GoogleCredentials.fromStream(is))
                  .build();
          
          // @formatter:on
      firebaseApp = FirebaseApp.initializeApp(options);
      log.info("MyFirebase config has been set up, name: " + firebaseApp.getName());
      return firebaseApp;
    } catch (Exception e) {
      log.error("FirebaseApp Exception, msg: " + e.getLocalizedMessage());
    }
    log.info("FirebaseApp is null. Do something...");
    return firebaseApp;
  }

  @Bean
  public FirebaseAuth firebaseAuth() {
    return FirebaseAuth.getInstance(firebaseApp());
  }

  @DependsOn(value = "firebaseApp")
  @Bean
  public Firestore firestore() {
    return FirestoreClient.getFirestore();
  }

}
