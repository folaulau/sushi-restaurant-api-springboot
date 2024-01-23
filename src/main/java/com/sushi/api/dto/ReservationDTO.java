package com.sushi.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class ReservationDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String uuid;

  private String name;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dateTime;

  private Integer numberOfPeople;

  private UserDTO user;

  private boolean deleted;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;


}
