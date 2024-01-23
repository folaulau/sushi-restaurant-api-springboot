package com.sushi.api.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushi.api.IntegrationTestConfiguration;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class AuthenticationTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  @Autowired
  @Qualifier(value = "xApiKey")
  private XApiKey xApiKey;

  @Transactional
  @Test
  void itShouldSignUp_valid() throws Exception {
    // Given

    String email = RandomGeneratorUtils.getRandomEmail();
    String password = "Test1234!";


  }

  @Transactional
  @Test
  void itShouldSignIn_valid() throws Exception {
    /**
     * Sign in
     */
  }

}
