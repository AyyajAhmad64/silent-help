# Silent Help

Anonymous student assistance platform built using Spring Boot, MySQL, HTML, CSS, JavaScript, and responsive UI principles.

Silent Help is a Spring Boot MVC platform for anonymous student support. Students can create help requests, answer others, save posts, chat privately, receive notifications, and report unsafe content. Admins can moderate users, posts, categories, reports, and deleted-account requests.

## Tech Stack

- Java 21
- Spring Boot 3.3
- Spring MVC, Security, Thymeleaf, Validation
- Spring Data JPA with MySQL
- Bootstrap 5, Bootstrap Icons, custom CSS/JS

## Quick Start

1. Install Java 21 or newer and Maven.
2. Start MySQL on port `3306`.
3. Create the database if needed:

```sql
CREATE DATABASE silent_help;
```

4. Confirm credentials and port in `src/main/resources/application.properties`.
5. Run the app:

```bash
mvn spring-boot:run
```

6. Open `http://localhost:8086`.

Seed data is enabled by default. Configure production credentials with environment variables before sharing or deploying.

## Useful Commands

```bash
mvn test
mvn -DskipTests package
mvn spring-boot:run
```

The packaged jar is written to `target/silenthelp-0.0.1-SNAPSHOT.jar`.

## Project Structure

- `src/main/java/com/silenthelp/silenthelp/controller` - MVC controllers
- `src/main/java/com/silenthelp/silenthelp/service` - business logic
- `src/main/java/com/silenthelp/silenthelp/model` - JPA entities
- `src/main/java/com/silenthelp/silenthelp/repository` - Spring Data repositories
- `src/main/resources/templates` - Thymeleaf views
- `src/main/resources/static/css` - application styling
- `src/main/resources/static/js` - browser behavior
- `database_schema.sql` - reference schema

## Core Workflows

- Browse or search help requests.
- Create academic, notes, placement, hostel, financial, lost item, or general support requests.
- Post public responses with optional anonymity.
- Save useful requests and mark accepted answers.
- Start private chats from request/response pages.
- Review notifications for replies and chat activity.
- Report abusive requests or responses.
- Admins can manage users, categories, hidden posts, reports, and deleted accounts.

## Deployment

The app supports cloud-provided ports and environment variables:

```text
PORT=8086
DB_URL=jdbc:mysql://localhost:3306/silent_help?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=<your-local-password>
DEFAULT_ADMIN_PASSWORD=<strong-admin-password>
```

## Additional Docs

- `INSTRUCTIONS.md` contains setup, testing, and troubleshooting notes.
- `MANUAL.md` contains student/admin workflow guidance.
- `HELP.md` keeps Spring Boot reference links generated with the project.
