package com.zst.configcenter.demo;

import com.zst.configcenter.client.annotation.EnableZstConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@EnableConfigurationProperties(DProperties.class)
@SpringBootApplication
@EnableZstConfig
public class Application {
    @Autowired
    private DProperties p;
    @Value("${zst.bb}")
    private String bb;
    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationContext applicationContext;

    private String c;

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Application.class, args);
    }

    @Value("${zst.cc}")
    public void setC(String c) {
        this.c = c;
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener() {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
                System.out.println("bb: " + bb);
                System.out.println("p: " + p.getAa());
                System.out.println("c: " + c);
            }
        };
    }
}
