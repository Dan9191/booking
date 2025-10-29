# Booking Service (booking-service)

## Swagger

Доступен при запуске

http://localhost:8083/webjars/swagger-ui/index.html#/

## Описание

Сервис для управления бронирования. Эндпоинты доступны в зависимости от ролей: `ROLE_ADMIN` для создания, `ROLE_BOOKING_SERVICE` и `ROLE_USER` для просмотра.

## Сборка

Требуется Java 21 и Gradle.

### Сборка
```shell
./gradlew clean build
```

### Запуск
```shell
./gradlew bootRun
```

## Конфигурация

Настройки в `src/main/resources/application.yaml`:

| Переменная                                | Значение по умолчанию                                            | Описание                                      |
|-------------------------------------------|------------------------------------------------------------------|-----------------------------------------------|
| SERVER_PORT                               | 8083                                                             | Порт сервиса                                  |
| SPRING_APPLICATION_NAME                   | booking-service                                                  | Имя приложения                                |
| SPRING_R2DBC_URL                          | r2dbc:h2:mem:///hoteldb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE | URL базы данных H2                            |
| SPRING_R2DBC_USERNAME                     | sa                                                               | Пользователь базы данных                      |
| SPRING_R2DBC_PASSWORD                     |                                                                  | Пароль базы данных                            |
| SPRING_FLYWAY_ENABLED                     | true                                                             | Включение Flyway миграций                     |
| SPRING_FLYWAY_LOCATIONS                   | classpath:db/migration                                           | Расположение миграционных скриптов            |
| SPRING_FLYWAY_BASELINE_ON_MIGRATE         | true                                                             | Базовая миграция при необходимости            |
| EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE     | http://localhost:8761/eureka/                                    | URL Eureka сервера                            |
| MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE | health,info,metrics,env,build,git                                | Экспонируемые эндпоинты Actuator              |
