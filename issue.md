# Infusion Monitoring API Endpoint

This document outlines the requirements for creating a new API endpoint for Infusion Monitoring. This task is intended for a junior programmer or an AI model.

## 1. Endpoint

The base endpoint for this API will be: `/api/infusion-monitoring`

## 2. Data Model

A new entity `InfusionMonitoring` needs to be created. The table below describes the fields for this entity. Please use appropriate Java data types.

| Column Name                | Data Type       | Constraints / Notes                            |
| -------------------------- | --------------- | ---------------------------------------------- |
| `infusion_id`              | `String`        | Primary Key, auto-generated (e.g., "INF-001")  |
| `session`                  | `Session`       | Many-to-One relationship with `Session` entity |
| `time`                     | `LocalDateTime` | Not Null                                       |
| `glucose_value`            | `Float`         |                                                |
| `confirmation_rate_min_kg` | `Float`         |                                                |
| `rate_min_kg`              | `Float`         |                                                |
| `flow_rate_ml_hr`          | `Float`         |                                                |
| `adjustment_note`          | `String`        |                                                |
| `monitored_by`             | `User`          | Many-to-One relationship with `User` entity    |
| `created_at`               | `LocalDateTime` | Automatically set on creation                  |
| `created_by`               | `User`          | Many-to-One relationship with `User` entity    |
| `updated_at`               | `LocalDateTime` | Automatically set on update                    |
| `updated_by`               | `User`          | Many-to-One relationship with `User` entity    |
| `deleted_at`               | `LocalDateTime` | For soft delete                                |
| `deleted_by`               | `User`          | Many-to-One relationship with `User` entity    |
| `status`                   | `String`        | Enum-like: `ACTIVE`, `INACTIVE`, `DELETED`     |

**Sample Data:**

```
infusion_id | session_id | time                | glucose_value | confirmation_rate_min_kg | rate_min_kg | flow_rate_ml_hr | adjustment_note                 | monitored_by | created_at          | created_by | updated_at          | updated_by | deleted_at | deleted_by | status
------------|------------|---------------------|---------------|--------------------------|-------------|-----------------|---------------------------------|--------------|---------------------|------------|---------------------|------------|------------|------------|---------
INF-001     | 101        | 2026-05-21 09:30:00 | 85            | 2                        | 2           | 140             | Mulai infus Dekstrosa 10%       | 5            | 2026-05-21 07:10:00 | 5          | 2026-05-21 07:10:00 | 5          |            |            | ACTIVE
INF-002     | 101        | 2026-05-21 09:40:00 | 83            | 2.5                      | 2.5         | 175             | Naikkan rate, glukosa turun     | 5            | 2026-05-21 07:10:00 | 5          | 2026-05-21 07:10:00 | 5          |            |            | ACTIVE
...
```

## 3. Implementation Steps

Follow the existing project structure and conventions. Create the following components under a new `infusionmonitoring` package:

1.  **Model (`InfusionMonitoring.java`):**
    - Create the JPA entity class in `com.tujuhsembilan.glucoseclamp.model`.
    - Define all fields with correct data types and annotations (`@Entity`, `@Id`, `@ManyToOne`, etc.).
    - Include auditing fields (`createdAt`, `createdBy`, etc.) similar to other models.

2.  **Repository (`InfusionMonitoringRepository.java`):**
    - Create the repository interface in `com.tujuhsembilan.glucoseclamp.repository`.
    - Extend `JpaRepository` and `JpaSpecificationExecutor`.
    - Add a method for soft-deleting: `findAllByStatus(String status)`.

3.  **DTOs:**
    - Create request and response DTOs in `com.tujuhsembilan.glucoseclamp.dto.request` and `com.tujuhsembilan.glucoseclamp.dto.response`.
    - `InfusionMonitoringRequest.java`: For `add` and `update` operations.
    - `InfusionMonitoringResponse.java`: For returning data.
    - `UpdateStatusRequest.java`: A simple DTO with a `status` field.

4.  **Service (`InfusionMonitoringService.java`):**
    - Create the service class in `com.tujuhsembilan.glucoseclamp.service`.
    - Implement the business logic for all features.
    - Use `ModelMapper` for converting between entities and DTOs.
    - Handle data validation and exceptions.

5.  **Controller (`InfusionMonitoringController.java`):**
    - Create the controller in `com.tujuhsembilan.glucoseclamp.controller.infusionmonitoring`.
    - Define the REST endpoints for each feature.
    - Use `@RestController` and `@RequestMapping("/api/infusion-monitoring")`.
    - Ensure proper use of HTTP methods (`GET`, `POST`, `PUT`, `DELETE`).

## 4. API Features & Endpoints

### 4.1. Get All Infusion Monitoring Records

- **Endpoint:** `GET /api/infusion-monitoring`
- **Description:** Retrieves a paginated list of all infusion monitoring records.
- **Response:** `200 OK` with a list of `InfusionMonitoringResponse`.

### 4.2. Get Infusion Monitoring by ID

- **Endpoint:** `GET /api/infusion-monitoring/{id}`
- **Description:** Retrieves a single infusion monitoring record by its ID.
- **Response:** `200 OK` with `InfusionMonitoringResponse`, or `404 Not Found`.

### 4.3. Add New Infusion Monitoring Record

- **Endpoint:** `POST /api/infusion-monitoring`
- **Description:** Creates a new infusion monitoring record.
- **Request Body:** `InfusionMonitoringRequest`
- **Response:** `201 Created` with the created `InfusionMonitoringResponse`.

### 4.4. Update Infusion Monitoring Record

- **Endpoint:** `PUT /api/infusion-monitoring/{id}`
- **Description:** Updates an existing infusion monitoring record.
- **Request Body:** `InfusionMonitoringRequest`
- **Response:** `200 OK` with the updated `InfusionMonitoringResponse`.

### 4.5. Soft Delete Infusion Monitoring Record

- **Endpoint:** `DELETE /api/infusion-monitoring/{id}`
- **Description:** Soft deletes a record by setting its status to `DELETED` and recording `deletedAt` and `deletedBy`.
- **Response:** `200 OK` or `204 No Content`.

### 4.6. Update Status

- **Endpoint:** `PUT /api/infusion-monitoring/{id}/status`
- **Description:** Updates the status of a record (`ACTIVE`, `INACTIVE`).
- **Request Body:** `UpdateStatusRequest` (e.g., `{ "status": "INACTIVE" }`)
- **Response:** `200 OK` with the updated `InfusionMonitoringResponse`.

### 4.7. Search by Keyword

- **Endpoint:** `GET /api/infusion-monitoring/search`
- **Description:** Searches for records based on a keyword. The search should apply to fields like `adjustment_note`.
- **Query Parameter:** `keyword` (e.g., `/api/infusion-monitoring/search?keyword=turun`)
- **Response:** `200 OK` with a list of matching `InfusionMonitoringResponse`.

## 5. General Requirements

- **Authentication & Authorization:** All endpoints must be secured. Ensure that only authenticated users with the correct permissions can access the endpoints.
- **Exception Handling:** Use the existing `ExceptionHandling` configuration to manage errors and return standardized error responses.
- **Validation:** Implement validation for request bodies.
- **Code Style:** Adhere to the existing code style, naming conventions, and project structure.
- **Unit Tests:** (Optional but recommended) Write basic unit tests for the service layer.
