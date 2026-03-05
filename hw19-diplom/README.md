на основе ДЗ-18
Проектная работа - Task Management System с авторизацией и ролями
То есть мини-«Trello», где пользователи создают проекты и задачи, назначают исполнителей, а данные защищены JWT и ролями.

Архитектура

Backend: Spring MVC + Spring Security (JWT)
DB: PostgreSQL / Hibernate / JPA
Docker: контейнеризация приложения и БД
API: REST

Функции безопасности: аутентификация по JWT, роли (USER, ADMIN)

Optional: Swagger для документации API

Схема компонентов

Controllers – REST API для проектов, задач, пользователей

Services – бизнес-логика (CRUD + валидация)

Repositories – JPA для работы с PostgreSQL

Security – JWT фильтр, UserDetailsService, PasswordEncoder

Docker – отдельные контейнеры для приложения и PostgreSQL

🔹 Функциональные возможности

Для пользователей:

Регистрация и авторизация (JWT)

CRUD проектов и задач

Назначение задач пользователям

Просмотр задач по проектам

Статусы задач (TODO, IN_PROGRESS, DONE)

Для администраторов:

Управление пользователями

Просмотр всех проектов и задач

Возможность изменять роли