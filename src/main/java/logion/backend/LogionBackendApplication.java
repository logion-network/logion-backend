package logion.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogionBackendApplication.class, args);
	}

}
