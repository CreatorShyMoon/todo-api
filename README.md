# Todo App

REST API для управления задачами на Spring Boot с JWT-аутентификацией.

## Стек технологий

- **Java 21**
- **Spring Boot 3.4** — фреймворк
- **Spring Security + JWT** — аутентификация
- **Spring Data JPA + Hibernate** — ORM
- **PostgreSQL / H2** — база данных
- **Docker + Docker Compose** — контейнеризация
- **JUnit 5 + Mockito** — тесты

## Структура проекта

```
todo-app/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── src/main/java/com/todo/
    ├── TodoApplication.java
    ├── model/
    │   ├── Todo.java
    │   └── User.java
    ├── repository/
    │   ├── TodoRepository.java
    │   └── UserRepository.java
    ├── service/
    │   ├── TodoService.java
    │   ├── TodoServiceImpl.java
    │   └── AuthService.java
    ├── controller/
    │   ├── TodoController.java
    │   └── AuthController.java
    ├── security/
    │   ├── SecurityConfig.java
    │   ├── JwtUtil.java
    │   ├── JwtAuthFilter.java
    │   └── CustomUserDetailsService.java
    ├── dto/
    │   ├── RegisterRequest.java
    │   ├── LoginRequest.java
    │   └── AuthResponse.java
    └── exception/
        └── TodoNotFoundException.java
```

## Запуск

### С Docker (рекомендуется)

```bash
docker-compose up --build
```

### Без Docker (профиль dev с H2)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Приложение доступно на

```
http://localhost:8080
```

## API Endpoints

### Аутентификация

| Метод | URL | Описание | Авторизация |
|-------|-----|----------|-------------|
| POST | `/api/auth/register` | Регистрация пользователя | Нет |
| POST | `/api/auth/login` | Логин (получение JWT) | Нет |

### Задачи

| Метод | URL | Описание | Авторизация |
|-------|-----|----------|-------------|
| GET | `/api/todos` | Получить все задачи | JWT |
| GET | `/api/todos/{id}` | Получить задачу по ID | JWT |
| POST | `/api/todos` | Создать задачу | JWT |
| PUT | `/api/todos/{id}` | Обновить задачу | JWT |
| DELETE | `/api/todos/{id}` | Удалить задачу | JWT |

## Примеры использования

### Регистрация

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"albert","email":"albert@mail.com","password":"123456"}'
```

Ответ:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "albert",
  "message": "Registration successful"
}
```

### Логин

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"albert","password":"123456"}'
```

### Создать задачу

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Изучить Spring Boot","description":"Пройти курс"}'
```

### Получить все задачи

```bash
curl http://localhost:8080/api/todos \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Обновить задачу

```bash
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Изучить Spring Boot","completed":true}'
```

### Удалить задачу

```bash
curl -X DELETE http://localhost:8080/api/todos/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Тесты

```bash
mvn test
```

## Конфигурация

### application.properties (PostgreSQL)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tododb
spring.datasource.username=todo_user
spring.datasource.password=todo_password
jwt.secret=your-secret-key
jwt.expiration=86400000
```

### application-dev.properties (H2)

```properties
spring.datasource.url=jdbc:h2:mem:devdb
spring.h2.console.enabled=true
```

H2 Console доступна на: `http://localhost:8080/h2-console`

## Лицензия

MIT
