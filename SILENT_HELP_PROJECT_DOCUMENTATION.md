# A Research Project Report On

# SILENT HELP - ANONYMOUS STUDENT SUPPORT AND ASSISTANCE PLATFORM

## In Partial Fulfillment Of

## Master of Computer Application

## By

**Ayyaj Kalandar Shaikh**  
MCA - I SEM - II  
Academic Year: 2025-26

## Under The Guidance Of

**Prof. Sarika Patil**

## Submitted To

**Savitribai Phule Pune University**  
**Dr. D. Y. Patil Institute of Management & Entrepreneur Development, Varale**

---

# CERTIFICATE

This is to certify that **Mr. Ayyaj Kalandar Shaikh** has successfully completed his Mini Project work entitled **"Silent Help - Anonymous Student Support and Assistance Platform"** in partial fulfillment of MCA - I SEM - II Mini Project for the academic year 2025-2026.

The project work has been carried out under our guidance and supervision. The work presented in this report is satisfactory and is recommended for evaluation.

Date: ____________

| Project Guide | HoD | Director |
| --- | --- | --- |
| Prof. Sarika Patil | Dr. Ashwini Chavan | Dr. Meghana Bhilare |

---

# SELF DECLARATION

I hereby declare that the project report entitled **"Silent Help - Anonymous Student Support and Assistance Platform"** submitted in partial fulfillment of the requirements for the degree of Master of Computer Application is my original work and has not been submitted previously to any other university or institution for the award of any degree or diploma.

The information submitted in this report is true and correct to the best of my knowledge.

Date: ____________  
Place: ____________

Signature of Student  
**Ayyaj Kalandar Shaikh**

---

# ACKNOWLEDGEMENT

I express my sincere gratitude to our Director **Dr. Meghana Bhilare**, HoD **Dr. Ashwini Chavan**, and project guide **Prof. Sarika Patil** for their valuable guidance, encouragement, and continuous support throughout the development of this project.

I would also like to thank the faculty members of the MCA Department, Dr. D. Y. Patil Institute of Management and Entrepreneur Development, for providing the required facilities and technical support.

Finally, I thank my family and friends for their motivation and encouragement during the completion of this project.

Signature of Student  
**Ayyaj Kalandar Shaikh**

---

# INDEX

| Chapter No. | Details | Page No. |
| --- | --- | --- |
| Chapter 1 | Introduction |  |
| 1.1 | Existing System and Need for System |  |
| 1.2 | Scope of System |  |
| 1.3 | Operating Environment |  |
| 1.4 | Brief Description of Technology Used |  |
| Chapter 2 | Proposed System |  |
| 2.1 | Study of Similar Systems |  |
| 2.2 | Feasibility Study |  |
| 2.3 | Objectives of Proposed System |  |
| 2.4 | Users of System |  |
| Chapter 3 | Analysis and Design |  |
| 3.1 | System Requirements |  |
| 3.2 | Entity Relationship Diagram |  |
| 3.3 | Table Structure / Schema |  |
| 3.4 | Use Case Diagram |  |
| 3.5 | Class Diagram |  |
| 3.6 | Activity Diagram |  |
| 3.7 | Sequence Diagram |  |
| 3.8 | Sample Input and Output Screens |  |
| Chapter 4 | Coding |  |
| 4.1 | Code Snippets |  |
| Chapter 5 | Testing |  |
| 5.1 | Test Cases |  |
| Chapter 6 | Limitations of Proposed System |  |
| Chapter 7 | Proposed Enhancements |  |
| Chapter 8 | Conclusion |  |
| Chapter 9 | Bibliography |  |
| Chapter 10 | User Manual |  |

---

# CHAPTER 1 - INTRODUCTION

## 1.1 Existing System and Need for System

In a college environment, students often need academic help, notes, placement guidance, technical assistance, hostel support, financial help, emotional support, and lost-and-found help. In the existing system, students usually depend on WhatsApp groups, social media posts, informal classroom communication, or direct personal contacts. These methods are fast, but they are not structured, searchable, or properly moderated.

Students may hesitate to ask for help publicly because of privacy concerns or fear of judgment. Important requests can get lost in large message groups. There is also no proper mechanism for categorizing requests, tracking replies, saving useful posts, reporting misuse, or managing private follow-up conversations.

The existing manual or informal system has the following limitations:

- No centralized platform for student support requests.
- No structured category-wise browsing.
- No anonymous public identity protection.
- Difficulty in tracking responses and accepted answers.
- No moderation system for abusive or fake content.
- No private communication linked to a request.
- No dashboard to track student activity, saved posts, notifications, and responses.

To overcome these problems, the proposed project **Silent Help** provides a secure web-based platform where students can post help requests, respond to others, stay anonymous in public, receive notifications, save useful requests, and communicate privately when needed.

## 1.2 Scope of System

The scope of the Silent Help system includes:

- Student registration and login.
- Role-based access for students and admin.
- Anonymous help request creation.
- Category-wise request browsing and search.
- Request priority, status, tags, campus group, and expected date management.
- Response posting with optional anonymity.
- Helpful votes and accepted answer workflow.
- Saved requests for later reference.
- Notifications for replies and account activity.
- Private chat between requester and responder.
- Report system for inappropriate requests or responses.
- Admin dashboard for moderation.
- User suspension/reactivation and deleted account review.
- Category management by admin.
- Responsive UI with light and dark mode support.

The system is designed mainly for college students, but the concept can be extended to departments, campus clubs, NGOs, and community support groups.

## 1.3 Operating Environment

### Hardware Requirements

| Component | Minimum Requirement |
| --- | --- |
| Processor | Intel Core i3 or above |
| RAM | 4 GB minimum |
| Hard Disk | 20 GB free storage |
| Network | Internet or local network connection |

### Software Requirements

| Component | Technology |
| --- | --- |
| Operating System | Windows 10/11 |
| Frontend | HTML5, CSS3, JavaScript, Bootstrap 5, Thymeleaf |
| Backend | Java 21, Spring Boot 3.3 |
| Database | MySQL |
| MySQL Username | root |
| MySQL Password | Set with `DB_PASSWORD` |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |
| Server | Embedded Apache Tomcat |
| Build Tool | Maven |
| IDE | IntelliJ IDEA / VS Code |
| Version Control | Git and GitHub |

## 1.4 Brief Description of Technology Used

### Java

Java is used as the main programming language for backend development. It provides object-oriented programming features, strong typing, and platform independence.

### Spring Boot

Spring Boot is used to develop the backend web application. It reduces configuration effort and provides built-in support for MVC, security, validation, JPA, and embedded server execution.

### Spring MVC

Spring MVC is used to handle web requests, controllers, routes, model data, and Thymeleaf page rendering.

### Spring Security

Spring Security is used for login, logout, authentication, role-based authorization, and account status handling.

### Thymeleaf

Thymeleaf is used as the server-side template engine to render dynamic HTML pages.

### MySQL

MySQL is used as the relational database for storing users, roles, categories, help requests, responses, reports, notifications, chats, and saved requests.

Database configuration used in this project:

```text
Database Name: silent_help
Username: root
Password: value of `DEFAULT_ADMIN_PASSWORD`
Server Port: 8086
```

### Bootstrap and CSS

Bootstrap and custom CSS are used for responsive layouts, navigation, forms, tables, cards, buttons, dark mode, and UI consistency.

### JavaScript

JavaScript is used for theme switching, form validation, response posting interaction, loading states, and alert behavior.

---

# CHAPTER 2 - PROPOSED SYSTEM

## 2.1 Study of Similar Systems

Students currently use different platforms such as WhatsApp, Telegram, Google Classroom, social media groups, and college notice boards for help and announcements. These platforms are useful for communication, but they are not designed for structured anonymous support.

Similar platforms generally lack:

- Request categories related to student needs.
- Anonymous public identity protection.
- Request status tracking.
- Accepted answer and helpful vote features.
- Admin moderation for reported content.
- Private chat connected to a help request.
- Saved request and contribution tracking.

Silent Help improves on these systems by combining request management, anonymity, moderation, private chat, notifications, and dashboards in a single platform.

## 2.2 Feasibility Study

### Technical Feasibility

The system is technically feasible because it is built using widely used and reliable technologies such as Java, Spring Boot, Thymeleaf, Bootstrap, and MySQL. These technologies support secure authentication, database connectivity, web rendering, validation, and modular development.

### Economic Feasibility

The project is economically feasible because all major tools and technologies used are open-source or freely available. Development can be completed using a personal computer and free IDEs.

### Operational Feasibility

The system is operationally feasible because it provides a simple user interface. Students can register, log in, create requests, browse help posts, respond, and manage their profiles with basic computer knowledge.

### Schedule Feasibility

The system can be developed in phases: requirement analysis, database design, frontend design, backend implementation, testing, and documentation. Therefore, it is suitable for a mini project timeline.

## 2.3 Objectives of Proposed System

The main objectives of Silent Help are:

- To provide a centralized student support platform.
- To allow students to ask for help without exposing identity publicly.
- To organize requests by category, urgency, tags, and status.
- To provide a response system for academic and campus support.
- To enable private communication when public replies are not enough.
- To notify users about replies and account activities.
- To allow students to save useful help requests.
- To provide admin moderation for reports, posts, users, and categories.
- To create a clean, responsive, and professional web interface.

## 2.4 Users of System

### Student

Student users can:

- Register and log in.
- Create anonymous or named help requests.
- Browse and search requests.
- Post responses.
- Save useful requests.
- Start private chats.
- View notifications.
- Update profile.
- Report inappropriate content.
- Request account deletion.

### Admin

Admin users can:

- View admin dashboard statistics.
- Manage student users.
- Suspend or activate accounts.
- Review deleted accounts.
- Hide or restore posts.
- Manage categories.
- Review and update report status.

---

# CHAPTER 3 - ANALYSIS AND DESIGN

## 3.1 System Requirements

### Functional Requirements

- User registration with allowed email domains.
- User login and logout.
- Student dashboard.
- Create help request.
- Browse and search help requests.
- Filter requests by category, urgency, and status.
- View request details.
- Post response to a request.
- Mark response as helpful.
- Accept response as answer.
- Save or unsave requests.
- Start private chat from request/response.
- Send private messages.
- View notifications.
- Report request or response.
- Update profile.
- Delete account request.
- Admin dashboard.
- Admin user management.
- Admin post moderation.
- Admin report review.
- Admin category management.

### Non-Functional Requirements

- Security: Only authenticated users can access protected pages.
- Usability: Interface should be simple and responsive.
- Maintainability: Code should be modular using MVC architecture.
- Performance: Pages should load quickly with optimized queries.
- Reliability: Responses and requests must be stored correctly.
- Accessibility: Text contrast and dark mode should remain readable.
- Scalability: System should allow new categories and features in future.

## 3.2 Entity Relationship Diagram

### Main Entities

- User
- Role
- Category
- HelpRequest
- Response
- ResponseVote
- SavedRequest
- Notification
- Report
- Conversation
- ChatMessage
- RequestView
- PasswordResetToken

### Relationships

- One user can create many help requests.
- One user can post many responses.
- One category can contain many help requests.
- One help request can have many responses.
- One help request can have one accepted response.
- One user can save many requests.
- One response can receive many helpful votes.
- One user can receive many notifications.
- One user can submit many reports.
- One conversation belongs to one help request.
- One conversation can have many chat messages.

### ERD Text Representation

```text
User 1 ---- * HelpRequest
User 1 ---- * Response
Category 1 ---- * HelpRequest
HelpRequest 1 ---- * Response
HelpRequest 1 ---- 0..1 AcceptedResponse
User * ---- * Role
User 1 ---- * SavedRequest ---- 1 HelpRequest
Response 1 ---- * ResponseVote ---- 1 User
User 1 ---- * Notification
User 1 ---- * Report
HelpRequest 1 ---- * Conversation
Conversation 1 ---- * ChatMessage
```

## 3.3 Table Structure / Schema

### users

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| username | VARCHAR(80) | Unique username |
| email | VARCHAR(140) | Unique email |
| password | VARCHAR(255) | Encrypted password |
| display_name | VARCHAR(120) | User display name |
| department | VARCHAR(120) | Department name |
| year_of_study | VARCHAR(40) | Year of study |
| enabled | BOOLEAN | Account active/suspended |
| deleted | BOOLEAN | Deleted account status |
| deletion_reason | VARCHAR(500) | Reason for deletion |
| created_at | DATETIME | Registration date |

### roles

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| name | VARCHAR/ENUM | STUDENT or ADMIN |

### categories

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| name | VARCHAR(80) | Category name |
| slug | VARCHAR(100) | URL-friendly slug |
| icon | VARCHAR(80) | Bootstrap icon class |
| color | VARCHAR(20) | Category color |
| active | BOOLEAN | Active/inactive category |

### help_requests

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| title | VARCHAR(150) | Request title |
| description | TEXT | Request details |
| anonymous | BOOLEAN | Public anonymous flag |
| urgency | VARCHAR(20) | LOW, NORMAL, HIGH, URGENT |
| expected_by | DATE | Expected date |
| preferred_contact | VARCHAR(120) | Preferred contact mode |
| tags | VARCHAR(180) | Tags |
| attachment_url | VARCHAR(255) | Attached file/image path |
| campus_group | VARCHAR(120) | Campus group/department |
| view_count | BIGINT | Number of views |
| response_count | BIGINT | Number of replies |
| status | ENUM | OPEN, IN_PROGRESS, RESOLVED, EXPIRED |
| hidden | BOOLEAN | Moderation visibility |
| student_id | BIGINT | Request owner |
| category_id | BIGINT | Category reference |

### responses

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| message | TEXT | Response text |
| anonymous | BOOLEAN | Anonymous response flag |
| hidden | BOOLEAN | Moderation visibility |
| helpful_count | BIGINT | Helpful vote count |
| created_at | DATETIME | Response date |
| request_id | BIGINT | Help request reference |
| student_id | BIGINT | Response author |

### notifications

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| title | VARCHAR(160) | Notification title |
| message | VARCHAR(260) | Notification message |
| link | VARCHAR(180) | Target page link |
| read_status | BOOLEAN | Read/unread flag |
| user_id | BIGINT | Receiver |

### reports

| Field Name | Data Type | Description |
| --- | --- | --- |
| id | BIGINT | Primary key |
| target_type | ENUM | REQUEST or RESPONSE |
| target_id | BIGINT | Reported item id |
| reason | VARCHAR(180) | Report reason |
| details | TEXT | Optional details |
| status | ENUM | OPEN, REVIEWED, DISMISSED |
| reporter_id | BIGINT | Reporting user |

## 3.4 Use Case Diagram

### Actors

- Student
- Admin

### Student Use Cases

- Register
- Login
- Create request
- Browse requests
- View request details
- Post response
- Save request
- Mark response helpful
- Start private chat
- View notifications
- Update profile
- Report content
- Delete account

### Admin Use Cases

- Login
- View dashboard
- Manage users
- Moderate posts
- Manage categories
- Review reports
- Reactivate deleted accounts

## 3.5 Class Diagram

### Main Classes

- User
- Role
- Category
- HelpRequest
- Response
- ResponseVote
- SavedRequest
- Notification
- Report
- Conversation
- ChatMessage

### Controller Classes

- AuthController
- PublicController
- DashboardController
- HelpRequestController
- ConversationController
- AdminController

### Service Classes

- UserService
- HelpRequestService
- ResponseService
- ConversationService
- NotificationService
- ReportService
- CategoryService
- FileStorageService

## 3.6 Activity Diagram

### Request Creation Activity

```text
Start
  |
Student Login
  |
Open Create Request Page
  |
Enter title, category, description, urgency, tags
  |
Submit Request
  |
Validate Input
  |
Save Request in Database
  |
Redirect to Request Details
  |
End
```

### Response Posting Activity

```text
Start
  |
Student Opens Request Details
  |
Types Response
  |
Clicks Post Response
  |
System Validates Non-Empty Message
  |
Save Response in Database
  |
Update Response Count
  |
Show Response in Thread
  |
Notify Request Owner
  |
End
```

## 3.7 Sequence Diagram

### Response Submission Sequence

```text
Student -> Browser: Type response and click Post Response
Browser -> HelpRequestController: POST /requests/{id}/responses
HelpRequestController -> ResponseService: respond(requestId, form, user)
ResponseService -> HelpRequestRepository: find request
ResponseService -> ResponseRepository: save response
ResponseService -> NotificationService: notify request owner
ResponseService -> HelpRequestController: return saved response
HelpRequestController -> Browser: success response / redirect
Browser -> Student: Display response in thread
```

## 3.8 Sample Input and Output Screens

### Input Screens

- Login page
- Registration page
- Create request form
- Response form
- Private chat message form
- Report request/response modal
- Admin category form

### Output Screens

- Home page
- Browse requests page
- Request details and responses page
- Student dashboard
- My requests page
- My responses page
- Notifications page
- Private chats page
- Admin dashboard
- Admin users page
- Admin reports page

---

# CHAPTER 4 - CODING

## 4.1 Code Snippets

### Response Form DTO

```java
public class ResponseForm {
    @NotBlank
    @Size(min = 5, max = 3000)
    private String message;

    private boolean anonymous = false;
}
```

### Response Service

```java
@Transactional
public Response respond(Long requestId, ResponseForm form, User student) {
    String message = form.getMessage() == null ? "" : form.getMessage().trim();
    if (message.isBlank()) {
        throw new IllegalArgumentException("Response cannot be empty.");
    }

    HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
    Response response = new Response();
    response.setHelpRequest(request);
    response.setStudent(student);
    response.setMessage(message);
    response.setAnonymous(form.isAnonymous());

    Response saved = responseRepository.save(response);
    request.incrementResponseCount();
    request.setUpdatedAt(LocalDateTime.now());
    return saved;
}
```

### Request Response Controller

```java
@PostMapping("/requests/{id}/responses")
public String respond(@PathVariable Long id,
                      @Valid @ModelAttribute ResponseForm responseForm,
                      BindingResult bindingResult,
                      Authentication authentication,
                      RedirectAttributes redirectAttributes,
                      Model model) {
    User viewer = currentUser(authentication);
    if (bindingResult.hasErrors()) {
        populateDetailsModel(id, viewer, model);
        model.addAttribute("error", "Please write a response between 5 and 3000 characters.");
        return "requests/details";
    }

    responseService.respond(id, responseForm, viewer);
    redirectAttributes.addFlashAttribute("success", "Response posted.");
    return "redirect:/requests/" + id;
}
```

### Security Configuration

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/about", "/contact", "/feedback",
                "/login", "/register", "/css/**", "/js/**", "/uploads/**")
        .permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated());
```

---

# CHAPTER 5 - TESTING

## 5.1 Test Cases

| Test Case ID | Test Scenario | Input | Expected Result | Status |
| --- | --- | --- | --- | --- |
| TC-01 | User registration | Valid student details | Account created | Pass |
| TC-02 | Invalid registration | Invalid email domain | Error message displayed | Pass |
| TC-03 | User login | Correct credentials | Dashboard opens | Pass |
| TC-04 | Invalid login | Wrong password | Login error displayed | Pass |
| TC-05 | Create request | Valid title, category, description | Request saved and details page opens | Pass |
| TC-06 | Browse requests | Open requests page | Request cards displayed | Pass |
| TC-07 | Search request | Keyword entered | Matching requests displayed | Pass |
| TC-08 | Post response | Valid response message | Response saved and shown in thread | Pass |
| TC-09 | Empty response | Blank message | Error message displayed | Pass |
| TC-10 | Save request | Click save | Request saved to dashboard | Pass |
| TC-11 | Helpful vote | Click helpful | Helpful count increases | Pass |
| TC-12 | Private chat | Send message | Message saved in chat thread | Pass |
| TC-13 | Notification open | Click notification | Target page opens and notification marked read | Pass |
| TC-14 | Report content | Submit report reason | Report saved for admin review | Pass |
| TC-15 | Admin user management | Suspend/reactivate user | User status updated | Pass |
| TC-16 | Admin post moderation | Hide/unhide post | Visibility updated | Pass |
| TC-17 | Admin category management | Add/toggle category | Category saved or updated | Pass |
| TC-18 | Dark mode | Toggle theme | UI readable in dark mode | Pass |

### Automated Testing

The project uses Spring Boot testing with MockMvc. The tests verify public pages, student pages, admin pages, and response posting functionality.

Command:

```bash
mvn test
```

---

# CHAPTER 6 - LIMITATIONS OF PROPOSED SYSTEM

The current system has the following limitations:

- The application is web-based and requires internet/local network access.
- Email/SMS notifications are not implemented.
- Mobile application is not available.
- AI-based spam or fake request detection is not implemented.
- File upload support is limited to request images/resources.
- Real-time chat uses normal page request flow, not WebSocket-based live messaging.
- Payment gateway integration is not included because the project focuses on student support and guidance.
- Advanced analytics and reporting charts are limited.

---

# CHAPTER 7 - PROPOSED ENHANCEMENTS

Future enhancements may include:

- Real-time chat using WebSocket.
- Email and SMS notification integration.
- Mobile application for Android/iOS.
- AI-based harmful content and fake request detection.
- Advanced admin analytics dashboard.
- Department-wise request routing.
- Mentor verification system.
- File preview for PDFs and documents.
- Cloud deployment using AWS/Azure/Render.
- Push notifications.
- Multi-language support.
- Advanced search filters and tags.

---

# CHAPTER 8 - CONCLUSION

Silent Help successfully provides a centralized, secure, and student-focused platform for anonymous help requests and campus support. It solves the problem of scattered informal communication by providing structured request creation, category-wise browsing, public anonymity, responses, saved posts, private chat, notifications, and admin moderation.

The project demonstrates the practical implementation of Java, Spring Boot, Thymeleaf, Spring Security, JPA, MySQL, Bootstrap, and JavaScript. It follows an MVC-based architecture and provides a scalable foundation for future improvements.

Overall, Silent Help is a useful student support platform that encourages collaboration while maintaining privacy, accountability, and moderation.

---

# CHAPTER 9 - BIBLIOGRAPHY

1. Spring Boot Official Documentation  
   https://spring.io/projects/spring-boot

2. Spring Security Documentation  
   https://spring.io/projects/spring-security

3. Spring Data JPA Documentation  
   https://spring.io/projects/spring-data-jpa

4. Thymeleaf Documentation  
   https://www.thymeleaf.org/documentation.html

5. MySQL Official Documentation  
   https://dev.mysql.com/doc/

6. Bootstrap Documentation  
   https://getbootstrap.com/docs/

7. Bootstrap Icons Documentation  
   https://icons.getbootstrap.com/

8. MDN Web Docs  
   https://developer.mozilla.org/

9. Apache Maven Documentation  
   https://maven.apache.org/guides/

10. Oracle Java Documentation  
    https://docs.oracle.com/en/java/

11. Roger S. Pressman, Software Engineering: A Practitioner's Approach, McGraw-Hill Education.

12. Ian Sommerville, Software Engineering, Pearson Education.

---

# CHAPTER 10 - USER MANUAL

## Step 1 - Open Application

Open the application in the browser:

```text
http://localhost:8086
```

Before running the application, start MySQL and confirm the following database credentials in `application.properties`:

```text
DB_USERNAME=root
DB_PASSWORD=<your-local-password>
```

## Step 2 - Register Student Account

The student clicks on **Join** or **Create student account** and enters:

- Username
- College email
- Password
- Display name
- Department
- Year of study

After successful registration, the user is redirected to the login page.

## Step 3 - Login

The student enters username/email and password. After successful login, the dashboard is displayed.

## Step 4 - Create Help Request

The student clicks **Create Request** and fills:

- Title
- Category
- Description
- Urgency
- Expected date
- Tags
- Campus group
- Attachment/resource link
- Anonymous option

After submission, the request becomes visible in the help feed.

## Step 5 - Browse Requests

Students can open the **Browse** page and search or filter requests by:

- Keyword
- Category
- Priority
- Status

## Step 6 - Post Response

On the request details page, the student writes a helpful response and clicks **Post Response**. The response is saved and immediately displayed in the response thread.

## Step 7 - Save Request

The student can click **Save** on useful requests. Saved requests are visible on the dashboard.

## Step 8 - Private Chat

If needed, students can start a private chat from the request/response page. Messages are visible in the private chat thread.

## Step 9 - Notifications

Students can open the notifications page to view updates about replies, helpful votes, account actions, and other activities.

## Step 10 - Profile Management

The student can update:

- Display name
- Department
- Year of study

The student can also request account deletion with a reason.

## Step 11 - Admin Login

Admin logs in and opens the admin dashboard.

## Step 12 - Admin Moderation

Admin can:

- View platform statistics.
- Manage users.
- Suspend or activate accounts.
- Review deleted accounts.
- Hide or restore posts.
- Manage categories.
- Review reports.

---

# APPENDIX - DEMO LOGIN DETAILS

These values depend on local seed configuration.

| Role | Username | Password |
| --- | --- | --- |
| Admin | admin | value of `DEFAULT_ADMIN_PASSWORD` |
| Student | student | Student@2026 |
