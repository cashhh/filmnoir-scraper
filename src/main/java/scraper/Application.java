package scraper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(Application.class, args);
        //new Scraper();
    }

    @Bean
    public CommandLineRunner test(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        return (args) -> {
            Scraper s = new Scraper(movieRepository, showtimeRepository);

        };
    }
}
