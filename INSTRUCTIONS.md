# Silent Help Instructions

## Running Locally

1. Install Java 21 or newer and Maven.
2. Start MySQL.
3. Create the database if it does not already exist:

```sql
CREATE DATABASE silent_help;
```

4. Check credentials in `src/main/resources/application.properties`.
5. Start the server:

```bash
mvn spring-boot:run
```

6. Visit `http://localhost:8080`.

## Testing

Run the full test suite before submitting changes:

```bash
mvn test
```

For packaging:

```bash
mvn -DskipTests package
```

The generated jar is written to `target/silenthelp-0.0.1-SNAPSHOT.jar`.

## Common Workflows

Profile issue checks:

- Login as `student` and open the profile menu.
- Click `Profile`.
- Confirm `/profile` loads without the generic error page.
- Update display name, department, or year and save.
- Confirm the success message remains long enough to read.

Account deletion checks:

- Login as a student.
- Open `Profile`.
- Enter a deletion reason of at least 10 characters.
- Submit deletion.
- Confirm the user is redirected to `/login?account=deleted_success`.
- Confirm the deletion message stays visible.
- Login as admin and review the entry under `Admin > Deleted Accounts`.

Moderation checks:

- Use `Admin > Users` to suspend and reactivate accounts.
- Use `Admin > Posts` to hide or restore help requests.
- Use `Admin > Reports` to review reported content.
- Use `Admin > Categories` to manage request categories.

## Troubleshooting

If a page shows `Something needs attention`, check the application console or `target/*.log` for the original exception. The generic error page only protects users from raw stack traces.

If Maven cannot download dependencies, check internet access or run the command again after Maven Central is reachable.

If login says an account was deleted, the user cannot log in until an admin reviews/reactivates the deleted account.
