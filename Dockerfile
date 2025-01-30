# Используем официальный образ OpenJDK 21
FROM openjdk:21-jdk
# Создаем рабочую директорию в контейнере
WORKDIR /app
# Копируем jar-файл микросервиса в контейнер
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
# Указываем команду для запуска микросервиса
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
