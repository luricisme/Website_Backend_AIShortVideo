package com.cabybara.aishortvideo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AishortvideoApplication {
    public static void main(String[] args) {
//		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        if (System.getenv("CI") == null) { // GitHub Actions có env CI=true
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        }
        SpringApplication.run(AishortvideoApplication.class, args);
    }
}
