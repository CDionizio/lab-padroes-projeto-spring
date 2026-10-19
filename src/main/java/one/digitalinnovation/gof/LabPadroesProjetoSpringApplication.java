package one.digitalinnovation.gof;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients(basePackages = "one.digitalinnovation.gof.service")
public class LabPadroesProjetoSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabPadroesProjetoSpringApplication.class, args);
    }

}
