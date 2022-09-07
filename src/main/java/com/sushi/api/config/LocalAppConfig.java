package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.sushi.api.library.aws.secretsmanager.FirebaseSecrets;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile(value = {"local"})
@Configuration
@PropertySource("classpath:config/local-secrets.properties")
public class LocalAppConfig {

  /** MySQL */
  @Value("${spring.datasource.username}")
  private String databaseUsername;

  @Value("${spring.datasource.password}")
  private String databasePassword;

  @Value("${spring.datasource.name}")
  private String databaseName;

  @Value("${spring.datasource.url}")
  private String databaseUrl;

  @Value("${aws.deploy.region:us-west-2}")
  private String targetRegion;

  @Value("${firebase.key.file.location}")
  private String firebaseKeyLocation;
  
  @Value("${firebase.web.api.key}")
  private String firebaseWebApiKey;
  
  @Bean(name = "stripeSecrets")
  public StripeSecrets stripeSecrets(@Value("${stripe.publishable.key}") String publishableKey,
      @Value("${stripe.secret.key}") String secretKey) {
    return new StripeSecrets(publishableKey, secretKey);
  }

  @Bean(name = "xApiKey")
  public XApiKey xApiKeySecrets(@Value("${web.x.api.key}") String webXApiKey,
      @Value("${mobile.x.api.key}") String mobileXApiKey,
      @Value("${utility.x.api.key}") String utilityXApiKey) {
    return new XApiKey(webXApiKey, mobileXApiKey, utilityXApiKey);
  }

  /* ================== datasource =============== */
  @Bean
  public HikariDataSource dataSource() {
    log.info("Configuring dataSource...");

    log.info("dbUrl={}", databaseUrl);
    log.info("dbUsername={}", databaseUsername);
    log.info("dbPassword={}", databasePassword);

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(databaseUrl);
    config.setUsername(databaseUsername);
    config.setPassword(databasePassword);
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

  private Regions getTargetRegion() {
    if (targetRegion == null) {
      targetRegion = "us-west-2";
    }
    return Regions.fromName(targetRegion);
  }

  @Bean
  public AWSCredentialsProvider amazonAWSCredentialsProvider() {
    return new ProfileCredentialsProvider("folauk110");
  }

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
        .withRegion(getTargetRegion()).build();
  }

  @Bean
  public AWSSecretsManager awsSecretsManager(AWSCredentialsProvider aWSCredentialsProvider) {
    String endpoint = "secretsmanager." + getTargetRegion().getName() + ".amazonaws.com";
    AwsClientBuilder.EndpointConfiguration config =
        new AwsClientBuilder.EndpointConfiguration(endpoint, getTargetRegion().getName());
    AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
    clientBuilder.setEndpointConfiguration(config);
    clientBuilder.setCredentials(aWSCredentialsProvider);
    return clientBuilder.build();
  }
  

  @Bean(name = "firebaseSecrets")
  public FirebaseSecrets firebaseSecrets() {
    FirebaseSecrets firebaseSecrets = new FirebaseSecrets();
    firebaseSecrets.setAuthWebApiKey(firebaseWebApiKey);
    return firebaseSecrets;
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
    try {
      // @formatter:off

          FirebaseOptions options = FirebaseOptions.builder()
                  .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseKeyLocation).getInputStream()))
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
