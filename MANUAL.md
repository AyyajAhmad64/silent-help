# Silent Help Manual

Silent Help is a Spring Boot MVC application for anonymous student support. Students can request help, answer others, save posts, chat privately, receive notifications, and report inappropriate content. Admins moderate users, posts, categories, reports, and deleted accounts.

## Quick Start

1. Start MySQL on port `3306`.
2. Confirm the default database settings in `src/main/resources/application.properties`.
3. Run the app:

```bash
mvn spring-boot:run
```

4. Open `http://localhost:8080`.

Seed data is enabled by default. Configure the admin password with `DEFAULT_ADMIN_PASSWORD` before deploying. Demo accounts:

- Admin: `admin` / value of `DEFAULT_ADMIN_PASSWORD`
- Student: `student` / `student@123`
- Mentor: `mentor` / `mentor@123`

## Student Guide

Students register with an allowed college email domain. After login, the dashboard shows open requests, resolved requests, urgent requests, helpful votes, accepted answers, saved posts, and recent activity.

Main student actions:

- Browse requests from the top navigation or dashboard.
- Create a request from `Requests` or the dashboard action card.
- Open a request to read details, reply, save it, report it, or start a private chat.
- Use `My Requests` to track posted requests.
- Use `My Responses` to review answers given to others.
- Use `Notifications` to open updates and mark them as read.
- Use `Profile` to update display name, department, and year of study.

## Account Deletion

Students can request account deletion from `Profile`. The account is locked immediately and the user is logged out. Admins keep a deletion record with the reason for review. To use Silent Help again after deletion, the student must wait 15 days and contact an admin for permission to reactivate/register again.

Important alert behavior:

- Login warning messages stay visible until the user leaves the page.
- Flash success messages with close buttons remain for 12 seconds.
- Flash error messages with close buttons remain for 30 seconds.

## Admin Guide

Admin pages are available from the `Admin` link after login.

Main admin actions:

- Dashboard: review totals and moderation status.
- Users: suspend or activate student accounts.
- Deleted Accounts: review deletion reasons and reactivate accounts when allowed.
- Posts: hide or restore request posts.
- Categories: create, edit, activate, or deactivate request categories.
- Reports: review, dismiss, or act on reported requests and responses.

## Developer Notes

Source layout:

- Java controllers: `src/main/java/com/silenthelp/silenthelp/controller`
- Business services: `src/main/java/com/silenthelp/silenthelp/service`
- JPA entities: `src/main/java/com/silenthelp/silenthelp/model`
- Thymeleaf templates: `src/main/resources/templates`
- CSS and JavaScript: `src/main/resources/static`
- Tests: `src/test/java`

Useful commands:

```bash
mvn test
mvn -DskipTests package
mvn spring-boot:run
```

The app uses Hibernate `ddl-auto=update` for local schema updates. A reference schema is kept in `database_schema.sql`.
