# 📋 Issue: Implementasi API Patients (CRUD + Pagination)

**Assignee:** Junior Developer  
**Priority:** High  
**Label:** `feature`, `backend`, `api`

---

## 🎯 Tujuan

Membuat fitur manajemen data **Pasien (Patients)** dengan endpoint REST API yang meliputi:

- Get All Patients dengan Pagination
- Add Patient (Create)
- Update Patient
- Delete Patient (Soft Delete)

---

## 📐 Skema Database

Tabel `patients` yang harus kamu buat di PostgreSQL:

| Kolom               | Tipe Data      | Keterangan                              |
| ------------------- | -------------- | --------------------------------------- |
| `patient_id`        | VARCHAR(10) PK | Primary Key, diisi manual (e.g. PAT001) |
| `medical_record_no` | VARCHAR(20)    | Nomor rekam medis (unique)              |
| `name`              | VARCHAR(255)   | Nama lengkap pasien                     |
| `gender`            | VARCHAR(10)    | `Male` atau `Female`                    |
| `dob`               | DATE           | Tanggal lahir (format: `YYYY-MM-DD`)    |
| `number_phone`      | VARCHAR(20)    | Nomor telepon                           |
| `created_at`        | TIMESTAMP      | Waktu data dibuat (auto-fill)           |
| `created_by`        | INTEGER        | ID user yang membuat                    |
| `updated_at`        | TIMESTAMP      | Waktu data diupdate (auto-fill)         |
| `updated_by`        | INTEGER        | ID user yang mengupdate                 |
| `deleted_at`        | TIMESTAMP      | Waktu data dihapus (nullable)           |
| `deleted_by`        | INTEGER        | ID user yang menghapus (nullable)       |
| `status`            | VARCHAR(10)    | `ACTIVE`, `INACTIVE` atau `DELETED`     |

**Contoh data:**

```
patient_id  | medical_record_no | name          | gender | dob        | number_phone | created_at          | created_by | updated_at          | updated_by | deleted_at | deleted_by | status
PAT001      | MR889100          | Adrian Saputra| Male   | 1998-06-10 | 8123456789   | 2026-05-21 07:10:00 | 1          | 2026-05-21 07:10:00 | 1          | null       | null       | ACTIVE
```

---

## 📁 Struktur File yang Perlu Dibuat

Ikuti pola yang sudah ada di project ini. Buat file di lokasi berikut:

```
src/main/java/com/tujuhsembilan/glucoseclamp/
├── model/
│   └── Patients.java                        ← [BUAT BARU]
├── repository/
│   └── PatientsRepository.java              ← [BUAT BARU]
├── dto/
│   ├── request/
│   │   ├── PatientRequest.java              ← [BUAT BARU]
│   │   └── PatientUpdateRequest.java        ← [BUAT BARU]
│   └── response/
│       └── PatientResponse.java             ← [BUAT BARU]
├── service/
│   └── PatientsService.java                 ← [BUAT BARU]
└── controller/
    └── patient/
        └── PatientsController.java          ← [BUAT BARU]
```

---

## 🗂️ Detail Implementasi Per File

---

### 1. `model/Patients.java`

Buat JPA Entity yang memetakan tabel `patients`. Ikuti pola dari `Users.java`.

**Ketentuan:**

- Gunakan anotasi `@Entity`, `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Gunakan `@EntityListeners(AuditingEntityListener.class)` untuk audit field
- `patient_id` bukan auto-generate sequence, gunakan `@Id` dan `@Column` saja (String)
- `created_at` → `@CreatedDate`
- `updated_at` → `@LastModifiedDate`
- `deleted_at` dan `deleted_by` boleh `null` (nullable = true)

```java
package com.tujuhsembilan.glucoseclamp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "patients")
@EntityListeners(AuditingEntityListener.class)
public class Patients implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "patient_id", unique = true, nullable = false, length = 10)
    private String patientId;

    @Column(name = "medical_record_no", unique = true, length = 20)
    private String medicalRecordNo;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "number_phone", length = 20)
    private String numberPhone;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @Column(name = "status", length = 10)
    private String status;
}
```

---

### 2. `repository/PatientsRepository.java`

Buat interface JPA Repository. Ikuti pola `UsersRepository.java`.

**Ketentuan:**

- Extend `JpaRepository<Patients, String>` (karena PK bertipe String)
- Tambahkan method untuk cek duplikasi `medicalRecordNo`
- Tambahkan method untuk query hanya data yang belum dihapus (soft delete)

```java
package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Patients;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientsRepository extends JpaRepository<Patients, String> {

    // Cek apakah nomor rekam medis sudah ada
    Boolean existsByMedicalRecordNo(String medicalRecordNo);

    // Ambil semua pasien yang statusnya ACTIVE (belum dihapus), support pagination
    Page<Patients> findAllByStatus(String status, Pageable pageable);
}
```

---

### 3. `dto/request/PatientRequest.java`

DTO untuk request **Add Patient**. Ikuti pola `RegisterRequest.java` (gunakan validasi Jakarta).

**Ketentuan:**

- Semua field wajib divalidasi menggunakan `@NotBlank`, `@NotNull`, `@Pattern`, dsb.
- `gender` hanya boleh `Male` atau `Female`
- `dob` bertipe `String` dengan format `YYYY-MM-DD`, konversi ke `LocalDate` di service

```java
package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {

    @NotBlank(message = "Patient ID tidak boleh kosong")
    @Size(max = 10, message = "Patient ID maksimal 10 karakter")
    private String patientId;

    @NotBlank(message = "Nomor rekam medis tidak boleh kosong")
    @Size(max = 20, message = "Nomor rekam medis maksimal 20 karakter")
    private String medicalRecordNo;

    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 255, message = "Nama maksimal 255 karakter")
    private String name;

    @NotBlank(message = "Gender tidak boleh kosong")
    @Pattern(regexp = "^(Male|Female)$", message = "Gender harus Male atau Female")
    private String gender;

    @NotBlank(message = "Tanggal lahir tidak boleh kosong")
    // Format: YYYY-MM-DD
    private String dob;

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    private String numberPhone;

    @NotNull(message = "created_by tidak boleh kosong")
    private Integer createdBy;
}
```

---

### 4. `dto/request/PatientUpdateRequest.java`

DTO untuk request **Update Patient**. `patientId` tidak perlu (diambil dari path variable).

```java
package com.tujuhsembilan.glucoseclamp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequest {

    @Size(max = 20, message = "Nomor rekam medis maksimal 20 karakter")
    private String medicalRecordNo;

    @Size(max = 255, message = "Nama maksimal 255 karakter")
    private String name;

    @Pattern(regexp = "^(Male|Female)$", message = "Gender harus Male atau Female")
    private String gender;

    // Format: YYYY-MM-DD
    private String dob;

    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    private String numberPhone;

    @NotNull(message = "updated_by tidak boleh kosong")
    private Integer updatedBy;
}
```

---

### 5. `dto/response/PatientResponse.java`

DTO untuk response data pasien yang dikembalikan ke client.

```java
package com.tujuhsembilan.glucoseclamp.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {
    private String patientId;
    private String medicalRecordNo;
    private String name;
    private String gender;
    private LocalDate dob;
    private String numberPhone;
    private String status;
    private String createdAt;
    private Integer createdBy;
    private String updatedAt;
    private Integer updatedBy;
}
```

---

### 6. `service/PatientsService.java`

Buat service yang berisi logika bisnis. Ikuti pola dari `UsersService.java`.

**Method yang harus dibuat:**

| Method             | Deskripsi                                                                  |
| ------------------ | -------------------------------------------------------------------------- |
| `getAllPatients()` | Ambil semua pasien aktif dengan pagination                                 |
| `addPatient()`     | Tambah pasien baru, cek duplikasi `medicalRecordNo`                        |
| `updatePatient()`  | Update data pasien berdasarkan `patientId`                                 |
| `deletePatient()`  | Soft delete: isi `deleted_at`, `deleted_by`, ubah `status` jadi `INACTIVE` |

**Ketentuan penting:**

- Gunakan anotasi `@Transactional` pada method yang write (add, update, delete)
- Gunakan `@Slf4j` untuk logging
- Kembalikan `ApiDataResponseBuilder` untuk semua response
- Untuk **Get All**, kembalikan `Page<PatientResponse>` di dalam `data`
- Untuk **soft delete**, **jangan** gunakan `repository.deleteById()`. Cukup update field `deletedAt`, `deletedBy`, dan `status = "INACTIVE"`

**Contoh struktur service:**

```java
package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.PatientRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.PatientUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.model.Patients;
import com.tujuhsembilan.glucoseclamp.repository.PatientsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PatientsService {

    @Autowired
    private PatientsRepository patientsRepository;

    // GET ALL PATIENTS (dengan pagination)
    public ApiDataResponseBuilder getAllPatients(int pageNumber, int pageSize) {
        // Gunakan PageRequest (0-indexed, tapi dari config sudah 1-indexed via properties)
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Patients> result = patientsRepository.findAllByStatus("ACTIVE", pageable);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data pasien")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    // ADD PATIENT
    @Transactional
    public ApiDataResponseBuilder addPatient(PatientRequest request) {
        // 1. Cek apakah patient_id sudah ada
        if (patientsRepository.existsById(request.getPatientId())) {
            return ApiDataResponseBuilder.builder()
                    .message("Patient ID sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // 2. Cek apakah medical_record_no sudah ada
        if (patientsRepository.existsByMedicalRecordNo(request.getMedicalRecordNo())) {
            return ApiDataResponseBuilder.builder()
                    .message("Nomor rekam medis sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // 3. Build entity dan simpan
        Patients patient = Patients.builder()
                .patientId(request.getPatientId())
                .medicalRecordNo(request.getMedicalRecordNo())
                .name(request.getName())
                .gender(request.getGender())
                .dob(LocalDate.parse(request.getDob()))
                .numberPhone(request.getNumberPhone())
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getCreatedBy())
                .status("ACTIVE")
                .build();

        patientsRepository.save(patient);
        log.info("Patient berhasil ditambahkan: {}", patient);

        return ApiDataResponseBuilder.builder()
                .data(patient)
                .message("Pasien berhasil ditambahkan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    // UPDATE PATIENT
    @Transactional
    public ApiDataResponseBuilder updatePatient(String patientId, PatientUpdateRequest request) {
        // 1. Cari patient berdasarkan ID
        Patients patient = patientsRepository.findById(patientId).orElse(null);

        if (patient == null || "INACTIVE".equals(patient.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data pasien tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        // 2. Update hanya field yang tidak null
        if (request.getMedicalRecordNo() != null) patient.setMedicalRecordNo(request.getMedicalRecordNo());
        if (request.getName() != null) patient.setName(request.getName());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getDob() != null) patient.setDob(LocalDate.parse(request.getDob()));
        if (request.getNumberPhone() != null) patient.setNumberPhone(request.getNumberPhone());
        patient.setUpdatedBy(request.getUpdatedBy());

        patientsRepository.save(patient);

        return ApiDataResponseBuilder.builder()
                .data(patient)
                .message("Data pasien berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    // DELETE PATIENT (soft delete)
    @Transactional
    public ApiDataResponseBuilder deletePatient(String patientId, Integer deletedBy) {
        Patients patient = patientsRepository.findById(patientId).orElse(null);

        if (patient == null || "INACTIVE".equals(patient.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data pasien tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        // Soft delete: isi deleted_at, deleted_by, dan ubah status
        patient.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        patient.setDeletedBy(deletedBy);
        patient.setStatus("INACTIVE");

        patientsRepository.save(patient);
        log.info("Patient {} berhasil dihapus (soft delete) oleh user {}", patientId, deletedBy);

        return ApiDataResponseBuilder.builder()
                .message("Pasien berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }
}
```

---

### 7. `controller/patient/PatientsController.java`

Buat REST Controller. Ikuti pola `UsersController.java`.

**Endpoint yang harus dibuat:**

| Method   | Path                    | Deskripsi                  |
| -------- | ----------------------- | -------------------------- |
| `GET`    | `/patients`             | Get all dengan pagination  |
| `POST`   | `/patients`             | Tambah pasien baru         |
| `PUT`    | `/patients/{patientId}` | Update data pasien         |
| `DELETE` | `/patients/{patientId}` | Hapus pasien (soft delete) |

```java
package com.tujuhsembilan.glucoseclamp.controller.patient;

import com.tujuhsembilan.glucoseclamp.dto.request.PatientRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.PatientUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.PatientsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patient", description = "Patient Management APIs")
@RestController
@RequestMapping("/patients")
public class PatientsController {

    @Autowired
    private PatientsService patientsService;

    // GET ALL PATIENTS dengan pagination
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllPatients(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = patientsService.getAllPatients(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    // ADD PATIENT
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addPatient(@Valid @RequestBody PatientRequest request) {
        ApiDataResponseBuilder result = patientsService.addPatient(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    // UPDATE PATIENT
    @PutMapping(path = "/{patientId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePatient(
            @PathVariable String patientId,
            @Valid @RequestBody PatientUpdateRequest request
    ) {
        ApiDataResponseBuilder result = patientsService.updatePatient(patientId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    // DELETE PATIENT (soft delete)
    @DeleteMapping(path = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deletePatient(
            @PathVariable String patientId,
            @RequestParam Integer deletedBy
    ) {
        ApiDataResponseBuilder result = patientsService.deletePatient(patientId, deletedBy);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
```

---

## ✅ Acceptance Criteria (Syarat Selesai)

Pastikan semua poin berikut terpenuhi sebelum PR diajukan:

- [ ] Tabel `patients` berhasil dibuat otomatis saat aplikasi dijalankan (`ddl-auto=update`)
- [ ] `GET /api/patients?pageNumber=1&pageSize=10` → mengembalikan daftar pasien dengan paginasi
- [ ] `POST /api/patients` → berhasil menambahkan pasien baru
- [ ] `POST /api/patients` → menolak jika `patient_id` atau `medical_record_no` sudah ada (HTTP 400)
- [ ] `PUT /api/patients/{patientId}` → berhasil mengupdate data pasien
- [ ] `PUT /api/patients/{patientId}` → mengembalikan 404 jika patient tidak ditemukan
- [ ] `DELETE /api/patients/{patientId}?deletedBy=1` → soft delete (status INACTIVE, `deleted_at` terisi)
- [ ] `DELETE /api/patients/{patientId}` → data tidak benar-benar dihapus dari DB
- [ ] Semua endpoint terdaftar di Swagger UI: `http://localhost:8080/api/glucoseclamp-documentation`

---

## 📦 Contoh Request & Response

### GET /api/patients?pageNumber=1&pageSize=10

**Response 200:**

```json
{
  "data": {
    "content": [
      {
        "patientId": "PAT001",
        "medicalRecordNo": "MR889100",
        "name": "Adrian Saputra",
        "gender": "Male",
        "dob": "1998-06-10",
        "numberPhone": "8123456789",
        "status": "ACTIVE",
        "createdAt": "2026-05-21T07:10:00",
        "createdBy": 1,
        "updatedAt": "2026-05-21T07:10:00",
        "updatedBy": 1
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  },
  "message": "Berhasil mendapatkan data pasien",
  "statusCode": 200
}
```

---

### POST /api/patients

**Request Body:**

```json
{
  "patientId": "PAT001",
  "medicalRecordNo": "MR889100",
  "name": "Adrian Saputra",
  "gender": "Male",
  "dob": "1998-06-10",
  "numberPhone": "8123456789",
  "createdBy": 1
}
```

**Response 200:**

```json
{
  "data": { ... },
  "message": "Pasien berhasil ditambahkan",
  "statusCode": 200
}
```

**Response 400 (duplikat):**

```json
{
  "message": "Nomor rekam medis sudah digunakan",
  "statusCode": 400
}
```

---

### PUT /api/patients/PAT001

**Request Body:**

```json
{
  "name": "Adrian Saputra Updated",
  "numberPhone": "8198765432",
  "updatedBy": 1
}
```

**Response 200:**

```json
{
  "data": { ... },
  "message": "Data pasien berhasil diupdate",
  "statusCode": 200
}
```

---

### DELETE /api/patients/PAT001?deletedBy=1

**Response 200:**

```json
{
  "message": "Pasien berhasil dihapus",
  "statusCode": 200
}
```

---

## ⚠️ Hal yang Perlu Diperhatikan

1. **Jangan gunakan `deleteById()`** untuk menghapus data. Gunakan soft delete (update field `deleted_at`, `deleted_by`, dan `status`).
2. **Base path API adalah `/api`** (sudah dikonfigurasi di `application.properties`), jadi endpoint kamu menjadi `/api/patients`.
3. **Pagination 1-indexed**: dari `application.properties` sudah dikonfigurasi `one-indexed-parameters=true`, tapi di dalam service kamu perlu ubah ke 0-indexed saat membuat `PageRequest` (`pageNumber - 1`).
4. **`@EntityListeners(AuditingEntityListener.class)`** wajib dipasang di entity agar `created_at` dan `updated_at` terisi otomatis.
5. Pastikan **`@EnableJpaAuditing`** sudah aktif di main class atau config (cek `GlucoseclampApplication.java`).

---

## 🔗 Referensi File yang Ada di Project

Gunakan file berikut sebagai referensi saat implementasi:

- **Model** → [`Users.java`](src/main/java/com/tujuhsembilan/glucoseclamp/model/Users.java)
- **Repository** → [`UsersRepository.java`](src/main/java/com/tujuhsembilan/glucoseclamp/repository/UsersRepository.java)
- **Request DTO** → [`RegisterRequest.java`](src/main/java/com/tujuhsembilan/glucoseclamp/dto/request/RegisterRequest.java)
- **Response DTO** → [`ApiDataResponseBuilder.java`](src/main/java/com/tujuhsembilan/glucoseclamp/dto/response/ApiDataResponseBuilder.java)
- **Service** → [`UsersService.java`](src/main/java/com/tujuhsembilan/glucoseclamp/service/UsersService.java)
- **Controller** → [`UsersController.java`](src/main/java/com/tujuhsembilan/glucoseclamp/controller/usermanagement/UsersController.java)
