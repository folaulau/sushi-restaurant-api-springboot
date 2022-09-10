package com.sushi.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class AddressCreateUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public boolean isValidAddress() {
        return (this.street != null && this.street.trim().isEmpty() == false) 
                && (this.city != null && this.city.trim().isEmpty() == false)
                && (this.state != null && this.state.trim().isEmpty() == false) 
                && (this.zipcode != null && this.zipcode.trim().isEmpty() == false) 
                && (this.longitude != null)
                && (this.latitude != null);
    }

}
