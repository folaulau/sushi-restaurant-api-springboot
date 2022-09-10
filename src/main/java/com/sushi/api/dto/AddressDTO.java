package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              id;

    private String            uuid;

    private String            street;

    private String            street2;

    private String            city;

    private String            state;

    private String            zipcode;

    private String            country;

    private Double            longitude;

    private Double            latitude;

    private String            timezone;

    private LocalDateTime     createdAt;

    private LocalDateTime     updatedAt;

}
