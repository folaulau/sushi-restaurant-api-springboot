package com.sushi.api.entity.user;

import com.sushi.api.dto.UserSignInDTO;
import com.sushi.api.dto.UserSignUpDTO;

public interface UserValidatorService {

    User validateSignUp(UserSignUpDTO userSignUpDTO);
    
    User validateSignIn(UserSignInDTO userSignInDTO);
}
