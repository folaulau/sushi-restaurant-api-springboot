package com.sushi.api.library.aws.secretsmanager;

import java.io.IOException;
import java.io.Serializable;

import com.sushi.api.utils.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Data
public class StripeSecrets implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String            publishableKey;

    private String            secretKey;

    private String            productId;

    private String            webhookSigningSecret;

    public static StripeSecrets fromJson(String json) {

        try {
            return ObjectUtils.getObjectMapper().readValue(json, StripeSecrets.class);
        } catch (IOException e) {
            log.error("SecretManager to Json exception", e);
            return null;
        }
    }

}
