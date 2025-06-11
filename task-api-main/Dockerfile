FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем необходимые утилиты
RUN apk add --no-cache ca-certificates openssl

WORKDIR /app

# Копируем jar и сертификаты
COPY build/libs/task-api-main-0.0.1-SNAPSHOT.jar app.jar
COPY rootca_ssl_rsa2022.cer /usr/local/share/ca-certificates/rootca_ssl_rsa2022.crt
COPY subca_ssl_rsa2022.crt /usr/local/share/ca-certificates/subca_ssl_rsa2022.crt

# Обновляем системные доверенные сертификаты
RUN update-ca-certificates

# Добавляем сертификаты в Java Truststore (cacerts)
# В Alpine путь к cacerts может отличаться, но чаще всего он стандартный:
RUN keytool -importcert -trustcacerts -noprompt \
    -alias russian_root_ca \
    -file /usr/local/share/ca-certificates/rootca_ssl_rsa2022.crt \
    -keystore ${JAVA_HOME}/lib/security/cacerts \
    -storepass changeit && \
    keytool -importcert -trustcacerts -noprompt \
    -alias russian_sub_ca \
    -file /usr/local/share/ca-certificates/subca_ssl_rsa2022.crt \
    -keystore ${JAVA_HOME}/lib/security/cacerts \
    -storepass changeit

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]

