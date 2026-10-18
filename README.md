# TaskManager API

REST API для управления проектами и задачами. 
Пользователи могут создавать проекты, добавлять участников и управлять задачами с
разграничением прав доступа.

## Стек технологий

- **Java**
- **Spring Boot**
- **Spring Data JPA / Hibernate**
- **Spring Security**
- **JWT** — аутентификация
- **PostgreSQL** — база данных
- **Maven** — сборка проекта
- **Postman** — тестирование API

## Запуск проекта

1. Установите JDK, PostgreSQL и Maven.
2. Создайте базу данных PostgreSQL.
3. Укажите настройки подключения к БД и JWT в конфигурации приложения.
4. Базовый адрес API: `http://localhost:8080`.

## Тестирование

API тестировалось вручную с помощью **Postman**.

Для тестирования необходимо:
- Запустить приложение и PostgreSQL.
- Зарегистрировать минимум двух пользователей.
- Авторизоваться и получить JWT-токены.
- Передавать токен в защищённых запросах через заголовок `Authorization: Bearer <token>`.

## API

### Авторизация
- `POST /api/auth/signup` — регистрация пользователя.
- `POST /api/auth/signin` — вход и получение токена.

### Пользователи
- `GET /api/users/me` — получить данные текущего пользователя.
- `GET /api/users/me/projects/{projectId}/members` — получить участников проекта.
- `POST /api/users/projects/{projectId}/add-user-to-project/{userId}` — добавить пользователя в проект.

### Проекты
- `GET /api/projects/find-projects-with-me` — получить проекты текущего пользователя.
- `GET /api/projects` — получить созданные проекты.
- `POST /api/projects` — создать проект.
- `PUT /api/projects/{projectId}` — обновить проект.
- `PUT /api/projects/{projectId}/update-status` — обновить статус проекта.
- `DELETE /api/projects/{projectId}` — удалить проект.

### Задачи
- `POST /api/tasks/project/{projectId}/create-task` — создать задачу.
- `GET /api/tasks/project/{projectId}/free-tasks` — получить свободные задачи проекта.
- `GET /api/tasks/project/{projectId}/task/{taskId}` — получить задачу по ID.
- `GET /api/tasks/my-tasks` — получить задачи текущего пользователя.
- `GET /api/tasks/project/{projectId}/tasks-for-project/user/{userId}` — получить задачи исполнителя в проекте.
- `PUT /api/tasks/project/{projectId}/task-update/{taskId}` — обновить данные задачи.
- `PATCH /api/tasks/{taskId}/status` — изменить статус задачи.
- `PATCH /api/tasks/{taskId}/priority` — изменить приоритет задачи.
- `DELETE /api/tasks/{taskId}` — удалить задачу.
- `PUT /api/tasks/task-update/{taskId}/set-user/{userId}` — назначить задачу пользователю.

## Особенности

- JWT-аутентификация.
- Проверка прав доступа к проектам и задачам.
- Управление участниками проектов.
- Назначение исполнителей и изменение статусов задач.
- Каскадное удаление связанных записей при удалении проекта.
