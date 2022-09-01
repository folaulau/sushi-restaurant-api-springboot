package com.sushi.api.entity.user;

import static org.springframework.http.HttpStatus.OK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;
import com.sushi.api.utils.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Users", description = "User Operations")
@RestController
@RequestMapping("/users")
public class UserRestController {

  @Autowired
  private UserService userService;

  @Operation(summary = "Authenticate", description = "sign up or sign in")
  @PostMapping(value = "/authenticate")
  public ResponseEntity<AuthenticationResponseDTO> authenticate(
      @RequestHeader(name = "x-api-key", required = true) String xApiKey,
      @RequestBody AuthenticatorDTO authenticatorDTO) {
    log.info("authenticate={}", ObjectUtils.toJson(authenticatorDTO));

    AuthenticationResponseDTO authenticationResponseDTO =
        userService.authenticate(authenticatorDTO);

    return new ResponseEntity<>(authenticationResponseDTO, OK);
  }
}
