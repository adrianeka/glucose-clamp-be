# Anamnesis API Endpoint

## Base URL

`/api/anamneses`

## Authentication

Authentication is required for all endpoints.

---

## Features

### 1. Get All Anamnesis

- **Method:** `GET`
- **Endpoint:** `/`
- **Description:** Retrieves a list of all anamnesis records.
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:**
    ```json
    [
      {
        "anamnesis_id": 1,
        "session_id": 101,
        "date": "2026-05-21",
        "chief_complaint": "Tidak ada keluhan (Sehat)",
        "medical_history": "Tidak ada",
        "assigned_by": 1,
        "created_at": "2026-05-21T07:10:00",
        "created_by": 1,
        "updated_at": "2026-05-21T07:10:00",
        "updated_by": 1,
        "deleted_at": null,
        "deleted_by": null,
        "status": "ACTIVE"
      }
    ]
    ```

### 2. Get Anamnesis by ID

- **Method:** `GET`
- **Endpoint:** `/{id}`
- **Description:** Retrieves a single anamnesis record by its ID.
- **URL Params:**
  - `id=[integer]` (required)
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:**
    ```json
    {
      "anamnesis_id": 1,
      "session_id": 101,
      "date": "2026-05-21",
      "chief_complaint": "Tidak ada keluhan (Sehat)",
      "medical_history": "Tidak ada",
      "assigned_by": 1,
      "created_at": "2026-05-21T07:10:00",
      "created_by": 1,
      "updated_at": "2026-05-21T07:10:00",
      "updated_by": 1,
      "deleted_at": null,
      "deleted_by": null,
      "status": "ACTIVE"
    }
    ```
- **Error Response:**
  - **Code:** `404 Not Found`
  - **Content:**
    ```json
    {
      "error": "Anamnesis not found"
    }
    ```

### 3. Add New Anamnesis

- **Method:** `POST`
- **Endpoint:** `/`
- **Description:** Creates a new anamnesis record.
- **Request Body:**
  ```json
  {
    "session_id": 102,
    "date": "2026-05-22",
    "chief_complaint": "Pusing",
    "medical_history": "Hipertensi",
    "assigned_by": 2
  }
  ```
- **Success Response:**
  - **Code:** `201 Created`
  - **Content:**
    ```json
    {
      "anamnesis_id": 2,
      "session_id": 102,
      "date": "2026-05-22",
      "chief_complaint": "Pusing",
      "medical_history": "Hipertensi",
      "assigned_by": 2,
      "created_at": "2026-05-22T08:00:00",
      "created_by": 2,
      "updated_at": "2026-05-22T08:00:00",
      "updated_by": 2,
      "deleted_at": null,
      "deleted_by": null,
      "status": "ACTIVE"
    }
    ```

### 4. Update Anamnesis

- **Method:** `PUT`
- **Endpoint:** `/{id}`
- **Description:** Updates an existing anamnesis record.
- **URL Params:**
  - `id=[integer]` (required)
- **Request Body:**
  ```json
  {
    "chief_complaint": "Sakit kepala ringan",
    "medical_history": "Hipertensi terkontrol"
  }
  ```
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:**
    ```json
    {
      "anamnesis_id": 2,
      "session_id": 102,
      "date": "2026-05-22",
      "chief_complaint": "Sakit kepala ringan",
      "medical_history": "Hipertensi terkontrol",
      "assigned_by": 2,
      "created_at": "2026-05-22T08:00:00",
      "created_by": 2,
      "updated_at": "2026-05-22T09:30:00",
      "updated_by": 2,
      "deleted_at": null,
      "deleted_by": null,
      "status": "ACTIVE"
    }
    ```

### 5. Soft Delete Anamnesis

- **Method:** `DELETE`
- **Endpoint:** `/{id}`
- **Description:** Soft deletes an anamnesis record by setting its status to `DELETED`.
- **URL Params:**
  - `id=[integer]` (required)
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:**
    ```json
    {
      "message": "Anamnesis deleted successfully"
    }
    ```

### 6. Search Anamnesis by Keyword

- **Method:** `GET`
- **Endpoint:** `/search`
- **Description:** Searches for anamnesis records based on a keyword. The search should cover `chief_complaint` and `medical_history`.
- **Query Params:**
  - `keyword=[string]` (required)
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:**
    ```json
    [
      {
        "anamnesis_id": 2,
        "session_id": 102,
        "date": "2026-05-22",
        "chief_complaint": "Sakit kepala ringan",
        "medical_history": "Hipertensi terkontrol",
        "assigned_by": 2,
        "created_at": "2026-05-22T08:00:00",
        "created_by": 2,
        "updated_at": "2026-05-22T09:30:00",
        "updated_by": 2,
        "deleted_at": null,
        "deleted_by": null,
        "status": "ACTIVE"
      }
    ]
    ```

### 7. Update Anamnesis Status

- **Method:** `PATCH`
- **Endpoint:** `/{id}/status`
- **Description:** Updates the status of an anamnesis record.
- **URL Params:**
  - `id=[integer]` (required)
- **Request Body:**
  ```json
  {
    "status": "INACTIVE"
  }
  ```
- **Success Response:**
  - **Code:** `200 OK`
  - **Content:**
    ```json
    {
      "anamnesis_id": 1,
      "session_id": 101,
      "date": "2026-05-21",
      "chief_complaint": "Tidak ada keluhan (Sehat)",
      "medical_history": "Tidak ada",
      "assigned_by": 1,
      "created_at": "2026-05-21T07:10:00",
      "created_by": 1,
      "updated_at": "2026-05-21T07:10:00",
      "updated_by": 1,
      "deleted_at": null,
      "deleted_by": null,
      "status": "INACTIVE"
    }
    ```
- **Notes:**
  - The `status` can be one of `ACTIVE`, `INACTIVE`, or `DELETED`.
  - This endpoint is intended for administrative purposes.

  ## Implementation

  Implemented files:
  - `src/main/java/com/tujuhsembilan/glucoseclamp/dto/request/AnamnesisRequest.java`
  - `src/main/java/com/tujuhsembilan/glucoseclamp/dto/request/AnamnesisUpdateRequest.java`
  - `src/main/java/com/tujuhsembilan/glucoseclamp/dto/request/AnamnesisStatusUpdateRequest.java`
  - `src/main/java/com/tujuhsembilan/glucoseclamp/dto/response/AnamnesisResponse.java`
  - `src/main/java/com/tujuhsembilan/glucoseclamp/service/AnamnesisService.java`
  - `src/main/java/com/tujuhsembilan/glucoseclamp/controller/anamnesis/AnamnesisController.java`
  - `src/main/java/com/tujuhsembilan/glucoseclamp/repository/AnamnesisRepository.java` (added paging & search queries)

  Notes:
  - Follows existing service/controller patterns used in the project (e.g., `VitalSignsService`).
  - Authentication required; uses current security principal to set `created_by/updated_by`.
  - Date parsing accepts `YYYY-MM-DD` for `date`.

  Next steps (optional):
  - Add unit/integration tests for service and controller.
  - Validate behavior with existing database schema and run application.
