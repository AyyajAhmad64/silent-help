# Silent Help

Anonymous Student Support & Assistance Platform for college students.

Silent Help is a Spring Boot MVC web app where students can anonymously ask for help with academics, notes, placement preparation, projects, financial requests, hostel/life issues, lost and found, and technical doubts. Public pages never expose student identity, while the database keeps ownership for notifications and moderation.

## Stack

- Java 21, Spring Boot 3.3.5, Spring MVC
- Spring Security with BCrypt password hashing
- Spring Data JPA / Hibernate
- Thymeleaf
- Bootstrap 5, Bootstrap Icons, custom CSS/JS
- MySQL, Maven, embedded Tomcat

## Main Modules

- `config`: Spring Security and sample data seeding
- `controller`: public, auth, student dashboard, requests, and admin routes
- `dto`: form objects with validation
- `model`: JPA entities for users, roles, categories, requests, responses, reports, notifications
- `repository`: Spring Data repositories
- `service`: business logic and transactional workflows
- `templates`: Thymeleaf pages and fragments
- `static`: CSS and JavaScript assets

## Setup

1. Create or verify MySQL credentials:
   - username: `root`
   - password: `admin@1234`
2. Start MySQL locally on port `3306`.
3. Run the application:

```bash
mvn spring-boot:run
```

4. Open:

```text
http://localhost:8080
```

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, so the schema is created automatically. A reference schema is also available in `database_schema.sql`.

## Demo Accounts

Seed data is enabled by default.

- Admin: `admin` / `admin@1234`
- Student: `student` / `student@123`
- Mentor: `mentor` / `mentor@123`

## Key Pages

- Public: Home, About, Login, Register
- Student: Dashboard, Create Request, Browse Requests, Request Details, My Requests, My Responses, Notifications, Profile
- Admin: Dashboard, Manage Users, Manage Posts, Manage Categories, Reports Management

## Screenshot Guide

- Home page: full-width blue campus hero, category tiles, latest anonymous help feed.
- Browse page: searchable, paginated request cards with category filters.
- Request details: anonymous request body, responses, report modals, and reply form.
- Student dashboard: sidebar navigation, activity stats, recent requests and responses.
- Admin dashboard: moderation metrics, reports, users, posts, and category management.

## Notes

This is not a mental health platform. It is scoped as a practical student-to-student support system with anonymity, moderation, and role-based administration.

## Manuals

- `MANUAL.md`: user, admin, and developer manual.
- `INSTRUCTIONS.md`: local run, test, and troubleshooting instructions.
