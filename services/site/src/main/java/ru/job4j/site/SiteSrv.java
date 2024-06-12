package ru.job4j.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class SiteSrv {
    private static final String SITE = "http://localhost:8080";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SiteSrv.class);
        application.addListeners(new ApplicationPidFileWriter("./site.pid"));
        application.run();
    }
}
