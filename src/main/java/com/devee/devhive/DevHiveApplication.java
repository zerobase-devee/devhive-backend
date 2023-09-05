package com.devee.devhive;

import com.devee.devhive.global.config.AppProperties;
import com.devee.devhive.global.config.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableConfigurationProperties({CorsProperties.class, AppProperties.class})
public class DevHiveApplication {

  public static void main(String[] args) {
    SpringApplication.run(DevHiveApplication.class, args);
  }

}
