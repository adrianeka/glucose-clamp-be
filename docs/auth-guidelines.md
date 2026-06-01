# Auth Guidelines

Dokumen ini menjelaskan pola auth yang dipakai di project Glucose Clamp supaya konsisten dan mudah diikuti oleh semua developer.

## Prinsip Utama

- JWT divalidasi di Spring Security filter, bukan di controller atau service.
- Controller tetap tipis: terima request, lalu panggil service.
- Service fokus ke business logic.
- Jangan sebar akses langsung ke `SecurityContextHolder` ke banyak class.
- Untuk ambil user login, pakai `CurrentUserService`.

## Aturan Pemakaian

### 1. Login / sign-in

- Hanya `UsersService.signIn(...)` yang bertugas melakukan autentikasi username/password.
- `AuthenticationManager` dipakai di sana untuk generate JWT.
- Endpoint login harus tetap `permitAll()`.

### 2. Request yang butuh user login

- Endpoint yang dilindungi harus membawa header `Authorization: Bearer <token>`.
- Token dibaca oleh `AuthTokenFilter`.
- Kalau token valid, Spring Security mengisi `SecurityContext`.

### 3. Ambil user login di code

Gunakan `CurrentUserService`:

- `getCurrentUserId()` jika hanya butuh id user untuk audit field.
- `getCurrentUserEntity()` jika butuh entity `User` untuk relasi JPA.
- `getCurrentUser()` kalau benar-benar butuh principal `UserDetailsImplement`.

### 4. Controller vs Service

- Kalau butuh user login di boundary HTTP, controller boleh ambil current user lalu kirim `userId` ke service.
- Kalau mau lebih seragam di project ini, service boleh ambil user lewat `CurrentUserService` langsung.
- Yang penting: jangan duplikasikan logika auth di banyak tempat.

## Pola yang Dipakai di Project Ini

### Patients

- `PatientsService` memakai `CurrentUserService.getCurrentUserId()` untuk mengisi `createdBy`, `updatedBy`, dan `deletedBy`.

### Session

- `SessionManagementService` memakai `CurrentUserService`.
- Service ini membuat `Session`, lalu generate `Activity` dari `protocols_detail`.
- `actor` untuk `Activity` diambil dari current user yang sudah authenticated.

## Do / Don't

### Do

- Pakai `CurrentUserService` untuk akses user login.
- Simpan audit field dengan `userId` yang valid.
- Jaga controller tetap tipis.
- Simpan login logic hanya di `UsersService`.

### Don't

- Jangan bikin service login baru kalau `UsersService` sudah ada.
- Jangan cast `UserDetailsImplement` langsung ke entity `User`.
- Jangan copy-paste `SecurityContextHolder` ke banyak service.
- Jangan taruh autentikasi username/password di service business seperti patient/session.

## Ringkasan Singkat

- `AuthTokenFilter` = validasi JWT.
- `UsersService.signIn(...)` = login dan generate JWT.
- `CurrentUserService` = ambil current user yang sudah login.
- `PatientsService` dan `SessionManagementService` = business logic yang butuh current user.
