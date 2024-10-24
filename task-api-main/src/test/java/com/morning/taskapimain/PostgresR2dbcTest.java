package com.morning.taskapimain;

import static junit.framework.TestCase.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.morning.taskapimain.Init.PostgresDatabaseContainerInitializer;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
@ContextConfiguration(initializers = PostgresDatabaseContainerInitializer.class)
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})  // Отключаем Kafka конфигурацию
public class PostgresR2dbcTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void someDatabaseTest() {
        User user = userRepository.findByUsername("admin").block();
        assertThat(user.getUsername()).isEqualTo("admin");
    }
    @Test
    void someDatabaseTest2() {
        Flux<User> userFlux = userRepository.findAll();

        // Блокируем результат и преобразуем его в список
        List<User> users = userFlux.collectList().block();

        // Проверяем, что пользователей 1
        assertNotNull(users);
        assertEquals(1, users.size(), "Expected exactly one user in the database");

        // Проверяем, что имя пользователя - admin
        assertEquals("admin", users.get(0).getUsername(), "Expected username to be 'admin'");
    }


}


