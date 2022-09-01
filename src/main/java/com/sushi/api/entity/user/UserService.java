package com.sushi.api.entity.user;

import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;

public interface UserService {

  AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

}
