# Royal Hotels - Java Spring Boot Backend

## Project Structure

```
backend/
 pom.xml                                          <- Maven build file (lists all dependencies)
 src/main/
   resources/
     application.properties                       <- Database and server settings
   java/com/royalhotels/backend/
     BackendApplication.java                      <- Main entry point (starts the server)
     CorsConfig.java                              <- Allows Angular to talk to this backend
     HealthController.java                        <- GET /api/health endpoint
```

## Step 1: Install Apache Maven

Maven is needed to download dependencies and run the project.

1. Go to https://maven.apache.org/download.cgi
2. Download the Binary zip archive (e.g., apache-maven-3.9.x-bin.zip)
3. Extract it to C:\Program Files\Apache\maven
4. Add C:\Program Files\Apache\maven\bin to your Windows PATH:
   - Search "Environment Variables" in the Start menu
   - Under System Variables, find Path and click Edit
   - Click New and add the path above
   - Click OK on all dialogs
5. Open a NEW PowerShell window and run: mvn -version

## Step 2: Set database credentials (do this EVERY time before running)

Open PowerShell in the backend folder and run:

    $env:DB_USERNAME = "root"
    $env:DB_PASSWORD = "your_mysql_password_here"

Replace your_mysql_password_here with your actual MySQL root password.

IMPORTANT: These are only set for the current PowerShell session.
           They are NOT stored permanently and are NOT saved in any file.

## Step 3: Run the backend

In the same PowerShell window (with credentials set above):

    cd C:\Users\LENOVO\Desktop\oopsieee\backend
    mvn spring-boot:run

The first run takes a few minutes because Maven downloads all dependencies.
When you see "Royal Hotels Backend is running!" the server is ready.

## Step 4: Test the health check

Open your browser and visit:
    http://localhost:8080/api/health

You should see a JSON response like:
    {
      "status": "UP",
      "message": "Royal Hotels backend is running.",
      "project": "ROYAL HOTELS - Where Comfort Meets Luxury",
      "timestamp": "2026-10-03 00:30:00"
    }

## Ports used

| Service         | Port |
|-----------------|------|
| Angular frontend| 4200 |
| Spring Boot API | 8080 |
| MySQL database  | 3306 |

## Java OOP concepts in this project

| Concept        | Where it appears                                        |
|----------------|---------------------------------------------------------|
| Class          | BackendApplication, CorsConfig, HealthController        |
| Object         | Spring creates objects of these classes automatically   |
| Method         | checkHealth() in HealthController                       |
| Encapsulation  | Each class has one clear responsibility                 |
| Abstraction    | Callers just use /api/health; do not see internal logic |