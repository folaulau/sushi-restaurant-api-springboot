package com.sushi.api.security.serveractivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile({"prod", "local"})
@Component
@Slf4j
public class ServerActivityCronJob {

    @Autowired
    private ServerActivityDAO serverActivityDAO;

    @Autowired
    private ServerActivityService serverActivityService;

    @Scheduled(fixedRate = CronJobRate.MINUTE * 2) // 300000 milliseconds is 5 minutes
    public void addLastActivityToS3() {

        ServerActivity lastActivity = serverActivityDAO.getLastActivity();

        serverActivityService.addActivityToS3(lastActivity);

    }
}
