package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sushi.api.entity.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class UserDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private String firstName;

  private String lastName;

  private String thirdPartyName;

  private String email;

  private String phoneNumber;
  
  @JsonFormat(pattern="yyyy-MM-dd")
  private LocalDate dob;

  private AccountDTO account;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;

  private UserStatus status;

  private AddressDTO address;

}
