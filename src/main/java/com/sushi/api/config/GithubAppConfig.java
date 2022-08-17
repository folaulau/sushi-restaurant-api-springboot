package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.sendgrid.SendGrid;
import com.sushi.api.library.aws.secretsmanager.AwsSecretsManagerService;
import com.sushi.api.library.aws.secretsmanager.FirebaseSecrets;
import com.sushi.api.library.aws.secretsmanager.SMTPSecrets;
import com.sushi.api.library.aws.secretsmanager.StripeSecrets;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"github"})
@Configuration
public class GithubAppConfig {

  @Value("${aws.deploy.region:us-west-2}")
  private String targetRegion;

  @Value("${database.username}")
  private String databaseUsername;

  @Value("${database.password}")
  private String databasePassword;

  @Value("${database.url}")
  private String databaseUrl;

  @Value("${spring.datasource.name}")
  private String databaseName;

  @Value("${aws.access.key}")
  private String awsAccessKey;

  @Value("${aws.secret.access.key}")
  private String awsSecretAccessKey;

  @Value("${firebase.web.api.key}")
  private String firebaseWebApiKey;

  @Value("${elasticsearch.host}")
  private String clusterNode;

  @Value("${elasticsearch.httptype}")
  private String clusterHttpType;

  @Value("${elasticsearch.username}")
  private String username;

  @Value("${elasticsearch.password}")
  private String password;

  @Value("${elasticsearch.port:9200}")
  private int clusterHttpPort;

  @Autowired
  private AwsSecretsManagerService awsSecretsManagerService;

  private Regions getTargetRegion() {
    return Regions.fromName(targetRegion);
  }

  @Bean(name = "amazonAWSCredentialsProvider")
  public AWSCredentialsProvider amazonAWSCredentialsProvider() {
    log.info("accessKey={}, secretKey={}", awsAccessKey, awsSecretAccessKey);
    return new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey));

  }

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
        .withRegion(getTargetRegion()).build();
  }

  @Bean
  public AmazonSimpleEmailService amazonSES() {
    return AmazonSimpleEmailServiceClientBuilder.standard()
        .withCredentials(amazonAWSCredentialsProvider()).withRegion(Regions.US_WEST_2).build();
  }

  /* ================== datasource =============== */
  @DependsOn("amazonAWSCredentialsProvider")
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

  @Bean(name = "xApiKey")
  public XApiKey xApiKeySecrets() {
    return awsSecretsManagerService.getXApiKeys();
  }

  @Bean(name = "stripeSecrets")
  public StripeSecrets stripeSecrets() {
    return awsSecretsManagerService.getStripeSecrets();
  }

  @Bean(name = "queue")
  public String queue(@Value("${queue}") String queue) {
    return queue;
  }

  @Bean(name = "firebaseSecrets")
  public FirebaseSecrets firebaseSecrets() {
    FirebaseSecrets firebaseSecrets = new FirebaseSecrets();
    firebaseSecrets.setAuthWebApiKey(firebaseWebApiKey);
    return firebaseSecrets;
  }

  @Bean
  public SendGrid sendGrid() {
    SMTPSecrets sMTPSecrets = awsSecretsManagerService.getSMTPSecrets();
    SendGrid sendGrid = new SendGrid(sMTPSecrets.getPassword());
    return sendGrid;
  }

  // @Bean
  // public JavaMailSender javaMailSender() {
  // JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
  // mailSender.setHost("smtp.sendgrid.net");
  // mailSender.setPort(587);
  //
  // SMTPSecrets sMTPSecrets = awsSecretsManagerService.getSMTPSecrets();
  //
  // mailSender.setUsername(sMTPSecrets.getUsername());
  // mailSender.setPassword(sMTPSecrets.getPassword());
  //
  // Properties props = mailSender.getJavaMailProperties();
  // props.put("mail.transport.protocol", "smtp");
  // props.put("mail.smtp.auth", "true");
  // props.put("mail.smtp.starttls.enable", "true");
  // props.put("mail.debug", "true");
  //
  // return mailSender;
  // }


}
