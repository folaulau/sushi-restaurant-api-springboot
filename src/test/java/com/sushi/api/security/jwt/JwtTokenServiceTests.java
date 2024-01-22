package com.sushi.api.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushi.api.IntegrationTestConfiguration;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;
import com.sushi.api.entity.role.Role;
import com.sushi.api.entity.role.UserType;
import com.sushi.api.entity.user.User;
import com.sushi.api.library.aws.secretsmanager.XApiKey;
import com.sushi.api.utils.ObjectUtils;
import com.sushi.api.utils.PasswordUtils;
import com.sushi.api.utils.RandomGeneratorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
public class JwtTokenServiceTests extends IntegrationTestConfiguration {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Transactional
    @Test
    void itShouldValidateToken() throws Exception {
        // Given

        String email = RandomGeneratorUtils.getRandomEmail();
        String password = "Test1234!";

        //@formatter:off
 
        User user = User.builder()
                .id(1L)
                .uuid(UUID.randomUUID().toString())
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .password(PasswordUtils.hashPassword(password))
                .roles(Set.of(Role.builder()
                        .userType(UserType.user)
                        .build()))
                .build();

        // @formatter:on

        String token = jwtTokenService.generateUserToken(user);

        assertThat(token).isNotNull();

        JwtPayload jwtPayload = jwtTokenService.getPayloadByToken(token);

        log.info("jwtPayload={}", jwtPayload);

        assertThat(jwtPayload).isNotNull();
        assertThat(jwtPayload.getSub()).isNotNull().isEqualTo(user.getId()+"");
        assertThat(jwtPayload.getUuid()).isNotNull().isEqualTo(user.getUuid());

    }

}
