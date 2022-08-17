package com.sushi.api.dto;

import java.io.Serializable;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@JsonInclude(value = Include.NON_NULL)
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Jwt token used for API calls.
     */
    private String            token;

    /**
     * user id
     */
    private Long              id;

    /**
     * user uuid
     */
    private String            uuid;

    private String            email;

    private Boolean           emailVerified;

    private Long              phoneNumber;

    private Boolean           phoneNumberVerified;

    private String            role;

    private String            status;
    
    private String            signUpStatus;

}
