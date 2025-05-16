# Используем минимальный Java-образ
FROM eclipse-temurin:17-jdk-alpine

# Установка рабочей директории
WORKDIR /app

# Копируем JAR-файл
COPY build/libs/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8761

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
