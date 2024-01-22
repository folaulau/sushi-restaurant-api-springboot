package com.sushi.api.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.sushi.api.validators.Email;
import com.sushi.api.validators.Password;
import com.sushi.api.validators.USPhoneNumber;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    @Email
    private String            email;
    
    @Password
    private String            password;

}
