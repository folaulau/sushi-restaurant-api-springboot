package com.sushi.api.entity.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sushi.api.dto.UserSignInDTO;
import com.sushi.api.dto.UserSignUpDTO;
import com.sushi.api.exception.ApiException;
import com.sushi.api.utils.PasswordUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserValidatorServiceImp implements UserValidatorService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public User validateSignUp(UserSignUpDTO userSignUpDTO) {

        Optional<User> optUser = userDAO.findByEmail(userSignUpDTO.getEmail().toLowerCase());

        if (optUser.isPresent()) {
            log.info("Email taken");
            throw new ApiException("Email taken", "an account has this email already", "Please use one email per account");
        }

        User user = User.builder().build();

        return user;
    }

    @Override
    public User validateSignIn(UserSignInDTO userSignInDTO) {

        Optional<User> optUser = userDAO.findByEmail(userSignInDTO.getEmail());

        User user = optUser.orElseThrow(() -> new ApiException("Email not found"));

        if (!user.getPassword().equals(PasswordUtils.hashPassword(userSignInDTO.getPassword()))) {
            log.info("invalid password");
            throw new ApiException("Invalid credentials");
        }

        return user;
    }

}
