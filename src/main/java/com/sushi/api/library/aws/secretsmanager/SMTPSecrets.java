package com.sushi.api.library.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.sushi.api.utils.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SMTPSecrets implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String username;

  private String password;

  public static SMTPSecrets fromJson(String json) {

    try {
      return ObjectUtils.getObjectMapper().readValue(json, SMTPSecrets.class);
    } catch (IOException e) {
      log.error("SecretManager to Json exception", e);
      return null;
    }
  }

}
