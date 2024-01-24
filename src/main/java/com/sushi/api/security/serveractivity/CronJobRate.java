package com.sushi.api.security.serveractivity;

public interface CronJobRate {

    long   SECOND                    = 1000;

    long   MINUTE                    = SECOND * 60;

    long   HOUR                      = MINUTE * 60;

    // 1:55pm Monday - Friday
    String CLOSE_OPEN_POSITIONS_TIME = "0 55 13 * * 1-5";
    
    String AT_CLOSE = "0 1 14 * * 1-5";

    // 7:20am Monday - Friday
    String OPEN_TIME = "0 30 7 * * 1-5";

    String _5_BEFORE_START_TIME = "0 25 7 * * 1-5";
    
    // 8am Monday - Friday
    String EIGHT_AM = "0 0 8 * * 1-5";
}
