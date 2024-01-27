package com.sushi.api.dto;

import java.io.Serializable;
import com.sushi.api.validators.Email;
import com.sushi.api.validators.Password;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
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
