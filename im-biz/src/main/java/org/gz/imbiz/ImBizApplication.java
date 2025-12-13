package org.gz.imbiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "org.gz.imcommon"
})
public class ImBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImBizApplication.class, args);
    }

}
