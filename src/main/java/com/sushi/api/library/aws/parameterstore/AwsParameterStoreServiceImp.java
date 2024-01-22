package com.sushi.api.library.aws.parameterstore;


import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AwsParameterStoreServiceImp implements AwsParameterStoreService{

    @Value("${stripe.secret.name:placeholder}")
    private String            stripeSecretName;

    @Value("${database.secret.name:placeholder}")
    private String            databaseSecretName;

    @Autowired
    private AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    @Override
    public DatabaseSecrets getDatabaseSecrets() {
        log.info("getDatabaseSecrets, key={}", databaseSecretName);

        String strValue = null;

        try {
            GetParameterRequest parameterRequest = new GetParameterRequest().withName(databaseSecretName);

            GetParameterResult getParameterResult = awsSimpleSystemsManagement.getParameter(parameterRequest);

            strValue = getParameterResult.getParameter().getValue();

            log.info("strValue={}", strValue);

        } catch (Exception e) {
            log.warn("Exception, msg={}",e.getMessage());
        }

        if(strValue!=null && !strValue.isEmpty()){

            try {

                return DatabaseSecrets.fromJson(strValue);

            } catch (Exception e) {
                log.warn("Exception, msg={}",e.getMessage());
            }
        }

        return null;
    }

    @Override
    public StripeSecrets getStripeSecrets() {
        log.info("getStripeSecrets, key={}", stripeSecretName);

        String strValue = null;

        try {
            GetParameterRequest parameterRequest = new GetParameterRequest().withName(stripeSecretName);

            GetParameterResult getParameterResult = awsSimpleSystemsManagement.getParameter(parameterRequest);

            strValue = getParameterResult.getParameter().getValue();

            log.info("strValue={}", strValue);

        } catch (Exception e) {
            log.warn("Exception, msg={}",e.getMessage());
        }

        if(strValue!=null && !strValue.isEmpty()){

            try {

                return StripeSecrets.fromJson(strValue);

            } catch (Exception e) {
                log.warn("Exception, msg={}",e.getMessage());
            }
        }

        return null;
    }
}
