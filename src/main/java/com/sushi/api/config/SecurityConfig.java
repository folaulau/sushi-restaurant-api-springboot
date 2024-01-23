package com.sushi.api.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import com.sushi.api.security.AuthorizationFilter;
import com.sushi.api.security.CustomAcccessDeniedHandler;
import com.sushi.api.security.CustomLogoutHandler;
import com.sushi.api.security.CustomLogoutSuccessHandler;
import com.sushi.api.utils.PathUtils;

@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter();
    }
    
    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration(AuthorizationFilter authorizationFilter) {
        FilterRegistrationBean<AuthorizationFilter> registration = new FilterRegistrationBean<>(authorizationFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // no cache between request
//        http.requestCache((cache) -> cache
//                .requestCache(new NullRequestCache())
//        );
        
//        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
//        http.cors(cor -> cor.disable());
        
        http
            .csrf(csrf -> csrf.disable())
//            .cors(cors -> cors.disable())
            // authenticate all routes and use WebSecurityCustomizer to ignore public routes
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .anyRequest().authenticated()
            );

        http.addFilterBefore(authorizationFilter(), org.springframework.security.web.access.intercept.AuthorizationFilter.class);

        return http.build();
    }
    
    /**
     * Use WebSecurityCustomizer to ignore public endpoints/routes and use authorizeHttpRequests to authenticate others
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                
                .requestMatchers(PathUtils.PING_URLS)
                .and()
                .ignoring()
                .requestMatchers(PathUtils.PUBLIC_URLS)
                .and()
                .ignoring()
                .requestMatchers(PathUtils.LOGIN_URLS)
                .and()
                .ignoring()
                .requestMatchers(PathUtils.SWAGGER_DOC_URLS);
        // ignore any request
        // return (web) -> web.ignoring().anyRequest();
    }


}
