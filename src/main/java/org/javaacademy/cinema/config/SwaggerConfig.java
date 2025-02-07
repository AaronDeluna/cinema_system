package org.javaacademy.cinema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi() {
        Contact contact = new Contact()
                .email("ivan_senior@yandex.ru")
                .name("Ivan");

        Info info = new Info()
                .title("Кинотеатр ки-но")
                .contact(contact)
                .description(
                        "Проект \"Кинотеатр ки-но\" представляет собой веб-приложение, "
                                + "которое позволяет пользователям управлять и взаимодействовать с"
                                + " расписанием сеансов кино, а также покупать билеты на фильмы в одном "
                                + "кинотеатре. В рамках проекта реализуется две основные роли: администратор "
                                + "и посетитель.");

        return new OpenAPI()
                .info(info);
    }
}
