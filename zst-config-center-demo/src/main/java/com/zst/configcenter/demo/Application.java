package com.zst.configcenter.demo;

import com.zst.configcenter.client.annotation.EnableZstConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableConfigurationProperties(DProperties.class)
@SpringBootApplication
@EnableZstConfig
public class Application {
    @Autowired
    private DProperties p;
    @Autowired
    private Prop2 p2;
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

//    @Bean
//    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener() {
//        return new ApplicationListener<ApplicationReadyEvent>() {
//            @Override
//            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//                System.out.println("bb: " + bb);
//                System.out.println("p: " + p.getAa());
//                System.out.println("c: " + c);
//            }
//        };
//    }

    @GetMapping("/")
    public String index() {
        return "bb: " + p2.getBb() + " p: " + p.getAa() + " c: " + c;
    }
}
