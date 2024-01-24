package com.sushi.api;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sushi.api.security.serveractivity.ServerActivity;
import com.sushi.api.security.serveractivity.ServerActivityDAO;
import com.sushi.api.security.serveractivity.ServerActivityService;

@SpringBootApplication
public class SushiRestaurantApiSpringbootApplication implements CommandLineRunner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(SushiRestaurantApiSpringbootApplication.class, args);
    }

    @Autowired
    @Qualifier(value = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ServerActivityService serverActivityService;

    @Order(Integer.MAX_VALUE)
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            // Display Environmental Useful Variables
            try {
                System.out.println("\n");
                Runtime runtime = Runtime.getRuntime();
                double mb = 1048576;// megabtye to byte
                double gb = 1073741824;// gigabyte to byte
                Environment env = ctx.getEnvironment();
                TimeZone timeZone = TimeZone.getDefault();

                String hasuraServerPort = env.getProperty("server.hasura.port");

                System.out.println("************************ Sushi API ***********************************");
                System.out.println("** Active Profile: " + Arrays.toString(env.getActiveProfiles()));
                System.out.println("** Port: " + env.getProperty("server.port"));
                System.out.println("** Timezone: " + timeZone.getID());
                System.out.println("** TimeStamp: " + new Date().toInstant().toString());
                if (Arrays.toString(env.getActiveProfiles()).equals("[local]")) {

                    System.out.println("** Internal Url: http://" + env.getProperty("project.host") + ":" + env.getProperty("server.port") + env.getProperty("server.servlet.context-path"));
                    System.out
                            .println("** External Url: http://" + InetAddress.getLocalHost().getHostAddress() + ":" + env.getProperty("server.port") + env.getProperty("server.servlet.context-path"));

                    System.out.println("** Internal Swagger: http://" + env.getProperty("project.host") + ":" + env.getProperty("server.port") + env.getProperty("server.servlet.context-path")
                            + "/swagger-ui/index.html");
                    System.out.println("** External Swagger: http://" + InetAddress.getLocalHost().getHostAddress() + ":" + env.getProperty("server.port")
                            + env.getProperty("server.servlet.context-path") + "/swagger-ui/index.html");

                } else {

                    System.out.println("** External Url: https://" + env.getProperty("project.host") + env.getProperty("server.servlet.context-path"));
                    System.out.println("** External Swagger: https://" + env.getProperty("project.host") + env.getProperty("server.servlet.context-path") + "/swagger-ui/index.html");

                }

                System.out.println("\n************************* Hasura *************************************");
                System.out.println("** (double check port in docker-composer.yaml file) ");
                System.out.println("** API Url: http://" + env.getProperty("project.host") + ":" + hasuraServerPort + "/v1/api");
                System.out.println("** Console Url: http://" + env.getProperty("project.host") + ":" + hasuraServerPort + "/console");
                System.out.println("** Health Check Url: http://" + env.getProperty("project.host") + ":" + hasuraServerPort + "/healthz");
                System.out.println();

                System.out.println("************************* Java - JVM *********************************");
                System.out.println("** Number of processors: " + runtime.availableProcessors());
                String processName = ManagementFactory.getRuntimeMXBean().getName();
                System.out.println("** Process ID: " + processName.split("@")[0]);
                System.out.println("** Total memory: " + (runtime.totalMemory() / mb) + " MB = " + (runtime.totalMemory() / gb) + " GB");
                System.out.println("** Max memory: " + (runtime.maxMemory() / mb) + " MB = " + (runtime.maxMemory() / gb) + " GB");
                System.out.println("** Free memory: " + (runtime.freeMemory() / mb) + " MB = " + (runtime.freeMemory() / gb) + " GB");
                System.out.println();
                System.out.println("************************* Thread Pool ********************************");
                System.out.println("** Thread Pool Core Size: " + taskExecutor.getCorePoolSize());
                System.out.println("** Thread Pool Active Count: " + taskExecutor.getActiveCount());
                System.out.println("** Thread Pool Max Size: " + taskExecutor.getMaxPoolSize());
                System.out.println("** Thread Pool Keep Alive Secs: " + taskExecutor.getKeepAliveSeconds());
                System.out.println("** Thread Pool Prefix: " + taskExecutor.getThreadNamePrefix());
                System.out.println("** Thread Pool Priority: " + taskExecutor.getThreadPriority());
                System.out.println();
                System.out.println("**********************************************************************");

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception, commandlineRunner -> " + e.getMessage());
            }
            System.out.println("\n");
        };
    }

    @Override
    public void run(String... args) throws Exception {
        
        serverActivityService.addApplicationStartUp();

        if (args == null || args.length == 0) {
            return;
        }

        System.out.println("************************* CommandLineRunner **************************");
        for (String arg : args) {
            System.out.println("arg: " + arg);
        }

        System.out.println("**********************************************************************");

    }

}
