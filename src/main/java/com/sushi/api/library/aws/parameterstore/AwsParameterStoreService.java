package com.sushi.api.library.aws.parameterstore;

public interface AwsParameterStoreService {

    public DatabaseSecrets getDatabaseSecrets();

    public StripeSecrets getStripeSecrets();
}
