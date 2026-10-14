# Mentor-Mentee Management System

A beginner-friendly **Java Spring Boot + MySQL + Thymeleaf** web application for managing mentor-mentee relationships in colleges.

> **WARNING**: Do not use sample credentials in a production environment.

---

## Prerequisites & Installation (VS Code on Windows)

### 1. Check Java Version
Ensure you have **Java 17** or later installed.
```powershell
java -version
```
If missing, install [Eclipse Temurin OpenJDK 17+](https://adoptium.net/) and ensure `JAVA_HOME` environment variable is set.

### 2. Check Maven
```powershell
mvn -version
```
If missing, download Apache Maven, extract it, and add its `bin` directory to your system `PATH`. Alternatively, use the **Extension Pack for Java** in Visual Studio Code.

### 3. Install & Start MySQL Server
Ensure MySQL Server is running locally on port `3306`.

### 4. Create Database
In MySQL Workbench or command line client, execute:
```sql
CREATE DATABASE IF NOT EXISTS mentor_mentee_db;
```
*(The application connection string also has `createDatabaseIfNotExist=true` configured).*

### 5. Configure Database Credentials
Open `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD` with your local MySQL password:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 6. Open in VS Code
1. Open Visual Studio Code.
2. Select **File** &rarr; **Open Folder...**
3. Select `MentorMenteeManagementSystem`.

### 7. Run the Application
In the VS Code terminal (or PowerShell):
```powershell
mvn spring-boot:run
```
Alternatively, open `MentorMenteeManagementSystemApplication.java` and click **Run** above the `main` method.

### 8. Access in Browser
Navigate to:
[http://localhost:8080/](http://localhost:8080/)

---

## Test Walkthrough Script

1. **Mentor Signup**:
   - Go to `http://localhost:8080/` &rarr; Click **Mentor Signup**.
   - Fill out the form and submit &rarr; See success message &rarr; Redirected to **Mentor Login**.
2. **Mentee Signup**:
   - Go to **Mentee Signup** &rarr; Select your newly registered mentor from the dropdown list.
   - Submit form &rarr; Redirected to **Mentee Login**.
3. **Mentor Login** (or use sample data: `ravi` / `password123`):
   - Access `/mentor/dashboard` &rarr; View assigned student mentees table.
   - Click student Roll Number or **View Profile** button &rarr; View read-only profile at `/mentor/mentee/{rollNo}`.
4. **Mentee Login** (or use sample data: `anjali` / `password123`):
   - Access `/mentee/dashboard` &rarr; View assigned faculty mentor.
   - Fill in LeetCode URL, GitHub URL, strengths, weaknesses, achievements, issues &rarr; Click **Save Changes**.
   - Refresh page to verify data persistence in MySQL.
5. **Security Verification**:
   - Log out and attempt to access `/mentor/dashboard` or `/mentee/dashboard` directly &rarr; Redirected to login.
   - Log in as a mentee and try to navigate to `/mentor/dashboard` &rarr; 403 Access Denied.

---

## Common Troubleshooting

| Problem | What to Check |
|---|---|
| `Communications link failure` | Is MySQL service running on port 3306? |
| `Access denied for user` | Check username and password in `src/main/resources/application.properties`. |
| `Unknown database` | Execute `CREATE DATABASE mentor_mentee_db;` in MySQL client. |
| Port 8080 in use | Change `server.port=8081` in `application.properties`. |
| 403 on dashboard | Role mismatch; ensure you logged in via the correct login page for your role. |
| Mentor dropdown empty | Register a mentor first or ensure sample `DataLoader` ran on startup. |
| Whitelabel error page | Check terminal logs for template mapping or typo errors. |
