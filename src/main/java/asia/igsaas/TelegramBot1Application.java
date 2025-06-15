package asia.igsaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
@EnableJpaRepositories
public class TelegramBot1Application {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBot1Application.class, args);
    }

}
