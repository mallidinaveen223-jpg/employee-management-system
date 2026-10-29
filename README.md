
# Employee Management System

A RESTful Employee Management System built using Java, Spring Boot, Spring Data JPA, and PostgreSQL.

## Project Overview

This project provides REST APIs to manage employee records. It demonstrates layered architecture, CRUD operations, input validation, exception handling, and unit testing.

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA and Hibernate
- PostgreSQL
- Maven
- JUnit 5 and Mockito
- IntelliJ IDEA
- Git and GitHub
- Postman

## Features

- Create a new employee
- Retrieve all employees
- Update an existing employee
- Delete an employee
- Validate employee input
- Handle employee-not-found exceptions
- Unit test service and controller layers

## REST API Endpoints

Base URL: `http://localhost:8080/api/employees`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/employees` | Create an employee |
| GET | `/api/employees` | Get all employees |
| PUT | `/api/employees/{id}` | Update an employee |
| DELETE | `/api/employees/{id}` | Delete an employee |

### Example Request Body

```json
{
  "name": "Naveen Kumar",
  "email": "naveen@example.com",
  "department": "IT",
  "salary": 50000
}
```

## Database Configuration

1. Install PostgreSQL and create a database named `employee_management_db`.
2. Configure your local database connection in `application.properties`.
3. Keep database credentials private. Never commit passwords or secrets to GitHub.
4. Ensure PostgreSQL is running before starting the application.

## Run the Application

Run the Spring Boot application from IntelliJ IDEA, or use Maven:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## Run Tests

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

The project includes unit tests for the service and controller layers.

## Project Structure

```text
src/main/java/
  controller/
  entity/
  exception/
  repository/
  service/

src/test/java/
  controller/
  service/
```

## Author

Naveen Kumar
