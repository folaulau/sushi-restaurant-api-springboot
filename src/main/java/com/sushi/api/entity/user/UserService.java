package com.sushi.api.entity.user;

import com.sushi.api.dto.AuthenticationResponseDTO;
import com.sushi.api.dto.AuthenticatorDTO;
import com.sushi.api.dto.UserDTO;
import com.sushi.api.dto.UserUpdateDTO;
import com.sushi.api.exception.ApiException;

public interface UserService {

  AuthenticationResponseDTO authenticate(AuthenticatorDTO authenticatorDTO);

  UserDTO getProfile(String uuid);
  
  User getByUuid(String uuid) throws ApiException;

  UserDTO updateProfle(UserUpdateDTO userUpdateDTO);

}
