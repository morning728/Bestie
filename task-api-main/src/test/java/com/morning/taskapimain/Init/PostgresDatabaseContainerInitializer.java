package com.morning.taskapimain.Init;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDatabaseContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("task_api_db")
            .withUsername("postgres")
            .withPassword("root")
            .withReuse(true); // Опция повторного использования контейнера

    static {
        postgresContainer.start();  // Стартуем контейнер в статическом блоке
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        // Настройка свойств для R2DBC и Flyway
        TestPropertyValues.of(
                "spring.r2dbc.url=" + getR2dbcUrl(),  // URL для R2DBC
                "spring.r2dbc.username=" + postgresContainer.getUsername(),
                "spring.r2dbc.password=" + postgresContainer.getPassword(),
                "spring.flyway.url=" + postgresContainer.getJdbcUrl(),  // URL для Flyway (JDBC)
                "spring.flyway.user=" + postgresContainer.getUsername(),
                "spring.flyway.password=" + postgresContainer.getPassword(),
                "spring.flyway.locations=classpath:migration"  // Локации для миграций
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

    private static String getR2dbcUrl() {
        // Генерация URL для R2DBC с динамически назначенным портом
        return "r2dbc:pool:postgres://" + postgresContainer.getHost() + ":" + postgresContainer.getMappedPort(5432) + "/" + postgresContainer.getDatabaseName();
    }
}

