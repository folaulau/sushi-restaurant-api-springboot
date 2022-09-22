package com.sushi.api.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class ServerStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private String status;

  private String rdsDBInstanceStatus;

  private int ecsDesiredCount;

  private int ecsRunningCount;

  private int ecsPendingCount;

  public ServerStatus(String status) {
    this.status = status;
    this.rdsDBInstanceStatus = "available";
    this.ecsDesiredCount = 1;
    this.ecsRunningCount = 1;

  }

}