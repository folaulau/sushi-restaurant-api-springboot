package com.sushi.api.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.UserSignInDTO;
import com.sushi.api.dto.UserSignUpDTO;
import com.sushi.api.entity.user.User;
import com.sushi.api.utils.ObjectUtils;
import com.sushi.api.utils.PasswordUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushi.api.IntegrationTestConfiguration;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

import com.github.javafaker.Faker;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureMockMvc
public class AuthenticationTests extends IntegrationTestConfiguration {

    @Autowired
    private MockMvc                mockMvc;

    @Resource
    private WebApplicationContext  webApplicationContext;

    @Autowired
    private Filter                 springSecurityFilterChain;

    @Autowired
    private ObjectMapper           objectMapper;

    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    @Autowired
    @Qualifier(value = "xApiKey")
    private XApiKey                xApiKey;

    private Faker                  faker = new Faker();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
    }

    @Transactional
    @Test
    void itShouldSignUp_valid() throws Exception {
        // Given

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = (firstName + lastName).toLowerCase() + "@gmail.com";
        String password = "Test1234!";

        UserSignUpDTO userSignUpDTO = UserSignUpDTO.builder().email(email).firstName(firstName).lastName(lastName).password(password).build();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users/signup")
                .header("x-api-key", "test-xapi-key")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectUtils.toJson(userSignUpDTO));

        MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        AuthenticationResponseDTO authenticationResponseDTO = objectMapper.readValue(contentAsString, new TypeReference<AuthenticationResponseDTO>() {});

        assertThat(authenticationResponseDTO).isNotNull();
        assertThat(authenticationResponseDTO.getEmail()).isNotNull().isEqualTo(email);
        assertThat(authenticationResponseDTO.getToken()).isNotNull().isNotEmpty();
        assertThat(authenticationResponseDTO.getUuid()).isNotNull().isNotEmpty();
        assertThat(authenticationResponseDTO.getId()).isNotNull().isGreaterThan(0);
        assertThat(authenticationResponseDTO.getStatus()).isNotNull().isEqualToIgnoringCase("active");
        assertThat(authenticationResponseDTO.getRole()).isNotNull().isEqualToIgnoringCase("user");
    }

    @Transactional
    @Test
    void itShouldSignIn_valid() throws Exception {
        /**
         * Sign in
         * 
         * folaudev+3@gmail.com is a preloaded user.
         */

      UserSignInDTO userSignInDTO = UserSignInDTO.builder()
              .email("folaudev+3@gmail.com")
              .password(PasswordUtils.DEV_PASSWORD)
              .build();

      RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users/signin")
              .header("x-api-key", "test-xapi-key")
              .accept(MediaType.APPLICATION_JSON)
              .characterEncoding("utf-8")
              .contentType(MediaType.APPLICATION_JSON)
              .content(ObjectUtils.toJson(userSignInDTO));

      MvcResult result = this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

      String contentAsString = result.getResponse().getContentAsString();
      AuthenticationResponseDTO authenticationResponseDTO = objectMapper.readValue(contentAsString, new TypeReference<AuthenticationResponseDTO>() {});

      assertThat(authenticationResponseDTO).isNotNull();
      assertThat(authenticationResponseDTO.getEmail()).isNotNull().isEqualTo(userSignInDTO.getEmail());
      assertThat(authenticationResponseDTO.getToken()).isNotNull().isNotEmpty();
      assertThat(authenticationResponseDTO.getUuid()).isNotNull().isNotEmpty();
      assertThat(authenticationResponseDTO.getId()).isNotNull().isGreaterThan(0);
      assertThat(authenticationResponseDTO.getStatus()).isNotNull().isEqualToIgnoringCase("active");
      assertThat(authenticationResponseDTO.getRole()).isNotNull().isEqualToIgnoringCase("user");
        
        
    }

}
