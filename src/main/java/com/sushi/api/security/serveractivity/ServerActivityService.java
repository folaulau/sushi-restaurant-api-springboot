package com.sushi.api.security.serveractivity;

import org.springframework.scheduling.annotation.Async;

public interface ServerActivityService {

    @Async
    void addApplicationStartUp();

    void addActivityToS3(ServerActivity serverActivity);
}
