package dev.magadiflo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories(basePackages = "dev.magadiflo.app.${section}")
@SpringBootApplication(scanBasePackages = {"dev.magadiflo.app.config", "dev.magadiflo.app.${section}"})
public class WebfluxPlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxPlaygroundApplication.class, args);
    }

}
