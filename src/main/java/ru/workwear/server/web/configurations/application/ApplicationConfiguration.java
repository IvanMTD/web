package ru.workwear.server.web.configurations.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.workwear.server.web.models.PageData;

@Slf4j
@Configuration
public class ApplicationConfiguration {
    @Bean
    public PageData pageData(){
        return new PageData();
    }
}
