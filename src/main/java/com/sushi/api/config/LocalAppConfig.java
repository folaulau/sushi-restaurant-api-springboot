package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
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

//  @Value("${spring.mail.username}")
//  private String smtpUsername;
//
//  @Value("${spring.mail.password}")
//  private String smtpPassword;

//  @Bean(name = "stripeSecrets")
//  public StripeSecrets stripeSecrets(@Value("${stripe.publishable.key}") String publishableKey,
//      @Value("${stripe.secret.key}") String secretKey, @Value("${stripe.product}") String productId,
//      @Value("${stripe.webhook.signing.key}") String webhookSigningKey) {
//    return new StripeSecrets(publishableKey, secretKey, productId, webhookSigningKey);
//  }


  @Bean(name = "xApiKey")
  public XApiKey xApiKeySecrets(
      @Value("${web.x.api.key}") String webXApiKey,
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

//  @Bean
//  public SendGrid sendGrid() {
//    SendGrid sendGrid = new SendGrid(smtpPassword);
//    return sendGrid;
//  }
//
//  @Bean
//  public JavaMailSender javaMailSender() {
//    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//    mailSender.setHost("smtp.sendgrid.net");
//    mailSender.setPort(465);
//    mailSender.setUsername(smtpUsername);
//    mailSender.setPassword(smtpPassword);
//
//    /**
//     * spring.mail.host=email-smtp.us-east-1.amazonaws.com
//     * spring.mail.properties.mail.transport.protocol=smtp spring.mail.properties.mail.smtp.port=587
//     * spring.mail.properties.mail.smtp.auth=true
//     * spring.mail.properties.mail.smtp.starttls.enable=true
//     * spring.mail.properties.mail.smtp.starttls.required=true
//     * spring.mail.properties.mail.sender=no-reply@
//     */
//    Properties props = mailSender.getJavaMailProperties();
//    props.put("mail.transport.protocol", "smtp");
//    props.put("mail.smtp.port", "465");
//    props.put("mail.smtp.auth", "true");
//    props.put("mail.smtp.ssl.trust", "smtp.sendgrid.net");
//    props.put("mail.smtp.starttls.enable", "true");
//    props.put("mail.smtp.starttls.required", "true");
//    props.put("mail.debug", "true");
//
//    return mailSender;
//  }
}
