# Bank_REST


**Bank_REST** — это RESTful backend-приложение на Java (Spring Boot), предназначенное для управления банковскими картами и пользователями. Система поддерживает аутентификацию и авторизацию (JWT), CRUD-операции с картами и пользователями, переводы между картами, а также подробную OpenAPI/Swagger-документацию.

---

## Основные функции

- Регистрация и аутентификация пользователей (JWT)
- Роли пользователей: `ADMIN` и `USER`
- CRUD-операции с банковскими картами
- Переводы между своими картами
- Просмотр баланса, фильтрация и пагинация карт
- Управление пользователями (для администратора)
- Безопасность: шифрование, маскирование номеров карт, ролевой доступ
- Документация OpenAPI (Swagger UI)
- Миграции БД через Liquibase
- Docker-окружение для быстрого запуска
- Юнит-тесты контроллеров и сервисов

---

## Используемые технологии и инструменты

- **Java 21**
- **Spring Boot 3.2.4**
- **Spring Data JPA**
- **Spring Security + JWT**
- **PostgreSQL**
- **Liquibase** (миграции)
- **springdoc-openapi** (Swagger UI/OpenAPI)
- **JUnit 5, Mockito** (тесты)
- **Docker, Docker Compose**

---

## Как запустить проект

1. **Клонируйте репозиторий:**
   ```bash
   git clone <repo-url>
   cd Bank_REST
   ```

2. **Создайте файл `.env` с переменными:**
   ```
   DATASOURCE_USERNAME=postgres
   DATASOURCE_PASSWORD=postgres
   SECRET=your_secret
   JWT_SECRET=your_jwt_secret
   JWT_LIFETIME=3600000
   ```

3. **Запустите через Docker Compose:**
   ```bash
   docker-compose up --build
   ```
    **Изначально создается пользователь для демонстрации работы и начального использования приложения:**
    
    `Username: admin`
    
    `Password: admin`


4. **API будет доступен по адресу:**  
   `http://localhost:8080`

5. **Swagger UI:**  
   `http://localhost:8080/swagger-ui.html`

6. **OpenAPI спецификация:**  
   `docs/openapi.yaml`  
   (или по адресу `/v3/api-docs` в формате JSON)

---

## Структура репозитория

```
Bank_REST/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/bankcards/
│   │   │   ├── controller/         # Контроллеры (UserController, CardController, AuthController)
│   │   │   ├── service/            # Сервисы и их реализации
│   │   │   ├── repository/         # Репозитории JPA
│   │   │   ├── entity/             # JPA-сущности (User, Card, Role, CardStatus)
│   │   │   ├── dto/                # DTO (UserDto, CardDto)
│   │   │   ├── util/requests/      # Request-классы (SaveUserRequest, LoginRequest и др.)
│   │   │   ├── util/response/      # Response-классы (AuthenticationResponse)
│   │   │   ├── security/           # JWT и конфиги безопасности
│   │   │   ├── config/             # Конфигурации (Security, OpenAPI)
│   │   │   └── exception/          # Кастомные исключения и обработчики
│   │   └── resources/
│   │       ├── application.yml     # Основной конфиг приложения
│   │       └── db/migration/       # Liquibase-миграции
│   └── test/
│       └── java/com/example/bankcards/
│           ├── controller/         # Тесты контроллеров
│           └── service/            # Тесты сервисов
│
├── docs/
│   └── openapi.yaml                # OpenAPI спецификация (Swagger)
│
├── docker-compose.yml              # Docker Compose для dev-окружения
├── Dockerfile                      # Dockerfile для сборки приложения
├── pom.xml                         # Maven-конфигурация
└── README.md                       # Этот файл
```

---

## Документация API

- **Swagger UI:**  
  `http://localhost:8080/swagger-ui.html`
- **OpenAPI спецификация:**  
  `docs/openapi.yaml`  
  (или `/v3/api-docs` для JSON)

---

## Миграции БД

- Все миграции находятся в `src/main/resources/db/migration/`
- Используется Liquibase, автоматически применяется при запуске приложения

---

## Тестирование

- Юнит-тесты для контроллеров и сервисов:  
  `src/test/java/com/example/bankcards/controller/`  
  `src/test/java/com/example/bankcards/service/`
- Тесты запускаются автоматически при сборке приложения, а так же при коммите на github.
