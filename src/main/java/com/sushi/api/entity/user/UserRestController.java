package com.sushi.api.entity.user;

import static org.springframework.http.HttpStatus.OK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;
import com.sushi.api.dto.UserDTO;
import com.sushi.api.dto.UserSignInDTO;
import com.sushi.api.dto.UserSignUpDTO;
import com.sushi.api.dto.UserUpdateDTO;
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

    @Operation(summary = "Sign Up", description = "sign up")
    @PostMapping(value = "/signup")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody UserSignUpDTO userSignUpDTO) {
        log.info("userSignUpDTO={}", ObjectUtils.toJson(userSignUpDTO));

        AuthenticationResponseDTO authenticationResponseDTO = userService.signUp(userSignUpDTO);

        return new ResponseEntity<>(authenticationResponseDTO, OK);
    }

    @Operation(summary = "Sign In", description = "sign in")
    @PostMapping(value = "/signin")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestHeader(name = "x-api-key", required = true) String xApiKey, @RequestBody UserSignInDTO userSignInDTO) {
        log.info("userSignInDTO={}", ObjectUtils.toJson(userSignInDTO));

        AuthenticationResponseDTO authenticationResponseDTO = userService.signIn(userSignInDTO);

        return new ResponseEntity<>(authenticationResponseDTO, OK);
    }

    @Operation(summary = "Get User Profile")
    @GetMapping(value = "/{uuid}")
    public ResponseEntity<UserDTO> getByUuid(@RequestHeader(name = "token", required = true) String token, @PathVariable String uuid) {
        log.info("getByUuid={}", uuid);

        UserDTO user = userService.getProfile(uuid);

        return new ResponseEntity<>(user, OK);
    }

    @Operation(summary = "Update User Profile")
    @PutMapping
    public ResponseEntity<UserDTO> updateProfile(@RequestHeader(name = "token", required = true) String token, @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("updateProfile={}", userUpdateDTO);

        UserDTO user = userService.updateProfle(userUpdateDTO);

        return new ResponseEntity<>(user, OK);
    }
}
