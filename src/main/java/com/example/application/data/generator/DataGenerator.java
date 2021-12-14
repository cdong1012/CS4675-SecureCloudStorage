package com.example.application.data.generator;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

// ignore this file
@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
//            userRepository.save(new User("user", "u", Role.USER));
//            userRepository.save(new User("admin", "a", Role.ADMIN));

            logger.info("Generated demo data");
        };
    }

}