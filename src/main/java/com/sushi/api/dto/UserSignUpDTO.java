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
public class UserSignUpDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String            firstName;
    
    private String            lastName;

    @Email
    private String            email;
    
    @USPhoneNumber
    private String            phoneNumber;
    
    @Password
    private String            password;

}
