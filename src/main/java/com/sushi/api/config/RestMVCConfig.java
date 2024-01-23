package com.sushi.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.sushi.api.utils.HttpRequestInterceptor;
import com.sushi.api.utils.HttpResponseErrorHandler;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@EnableWebMvc
@Configuration
public class RestMVCConfig {

  @Value("${resttemplate.timeout:100000}")
  private int restTemplateTimeout;


  @Bean
  public RestTemplate getRestTemplate() {
    final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(restTemplateTimeout);
    requestFactory.setReadTimeout(restTemplateTimeout);
    RestTemplate restTemplate =
        new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
    restTemplate.getInterceptors().add(new HttpRequestInterceptor());
    restTemplate.setErrorHandler(new HttpResponseErrorHandler());
    return restTemplate;
  }

//  @Profile("dev")
//  @Bean
//  public OpenAPI openDevAPI() {
//    return new OpenAPI().addServersItem(new Server().url("https://dev-api.poochapp.com/v1"));
//  }
//
//  @Profile("prod")
//  @Bean
//  public OpenAPI openProdAPI() {
//    return new OpenAPI().addServersItem(new Server().url("https://prod-api.poochapp.com/v1"));
//  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {

      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*").allowedMethods("*").allowedOrigins("*");
      }

      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // TODO Auto-generated method stub
        // WebMvcConfigurer.super.addResourceHandlers(registry);

          registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

          registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
      }

    };
  }


}
