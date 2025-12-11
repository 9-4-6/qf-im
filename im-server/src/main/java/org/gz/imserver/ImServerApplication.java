package org.gz.imserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author guozhong
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ImServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(ImServerApplication.class, args);
    }

}
