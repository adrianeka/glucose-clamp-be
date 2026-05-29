package com.tujuhsembilan.glucoseclamp.config;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedPatients();
        seedProtocols();
        seedSessions();
        seedDevices();
        seedProtocolDetails();
        syncSequences();
    }

    private void seedRoles() {
        batch(
                "INSERT INTO roles (role_id, role_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (role_id) DO UPDATE SET role_name = EXCLUDED.role_name, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, "Superadmin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, "Admin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, "Supervisor", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, "Operator Insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, "Operator Analyzer", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, "Operator Pump", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, "Operator Pump 2", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 6, "DELETED")
        );
    }

    private void seedUsers() {
        String password = passwordEncoder.encode("hash123");
        batch(
                "INSERT INTO users (user_id, role_id, name, username, email, password, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET role_id = EXCLUDED.role_id, name = EXCLUDED.name, username = EXCLUDED.username, email = EXCLUDED.email, password = EXCLUDED.password, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, 1, "Super Admin", "superadmin", "superadmin@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, 2, "Admin RSCM", "admin", "admin@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, 3, "Dr Budi", "budi", "budi@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, 4, "Fitri", "fitri", "fitri@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, 5, "Rina", "rina", "rina@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, 6, "Agus", "agus", "agus@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, 6, "Bagas", "bagas", "bagas@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "INACTIVE")
        );
        jdbcTemplate.update("UPDATE users SET password = ? WHERE password = ?", password, "PASSWORD_PLACEHOLDER");
    }

    private void seedSessions() {
    batch(
        "INSERT INTO sessions (session_id, patient_id, protocol_id, visit_date, start_time, end_time, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (session_id) DO UPDATE SET patient_id = EXCLUDED.patient_id, protocol_id = EXCLUDED.protocol_id, visit_date = EXCLUDED.visit_date, start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
        row(101, "PAT-001", "PR-24H", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
    );
}

    private void seedDevices() {
        batch(
                "INSERT INTO devices (device_id, device_type, device_brand, serial_number, last_calibration_date, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (device_id) DO UPDATE SET device_type = EXCLUDED.device_type, device_brand = EXCLUDED.device_brand, serial_number = EXCLUDED.serial_number, last_calibration_date = EXCLUDED.last_calibration_date, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, "Infusion Pump", "B. Braun Infusomat", "SN-991823", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, "Chemistry Analyzer", "Cobas C111", "SN-774122", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, "Infusion Pump", "Terumo TE-112", "SN-110099", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedPatients() {
        batch(
                "INSERT INTO patients (patient_id, medical_record_no, name, gender, dob, number_phone, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (patient_id) DO UPDATE SET medical_record_no = EXCLUDED.medical_record_no, name = EXCLUDED.name, gender = EXCLUDED.gender, dob = EXCLUDED.dob, number_phone = EXCLUDED.number_phone, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row("PAT-001", "MR889100", "Adrian Saputra", "Male", d("1998-06-10"), "8123456789", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedProtocols() {
        batch(
                "INSERT INTO protocols (protocol_id, protocol_code, protocol_name, insulin_dose_rule, insulin_dose_unit, glucose_target_min, glucose_target_max, glucose_target_unit, duration_hours, version, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (protocol_id) DO UPDATE SET protocol_code = EXCLUDED.protocol_code, protocol_name = EXCLUDED.protocol_name, insulin_dose_rule = EXCLUDED.insulin_dose_rule, insulin_dose_unit = EXCLUDED.insulin_dose_unit, glucose_target_min = EXCLUDED.glucose_target_min, glucose_target_max = EXCLUDED.glucose_target_max, glucose_target_unit = EXCLUDED.glucose_target_unit, duration_hours = EXCLUDED.duration_hours, version = EXCLUDED.version, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row("PR-24H", "EGC001", "Euglycemic Clamp", "0.5", "U/kgBW SC", bd("90"), bd("100"), "mg/dL", bd("24"), 1.0f, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedProtocolDetails() {
        batch(
                "INSERT INTO protocols_detail (protocols_detail_id, protocol_id, phase_code, time_interval, blood_raw, insulin_inject, insulin_check, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (protocols_detail_id) DO UPDATE SET protocol_id = EXCLUDED.protocol_id, phase_code = EXCLUDED.phase_code, time_interval = EXCLUDED.time_interval, blood_raw = EXCLUDED.blood_raw, insulin_inject = EXCLUDED.insulin_inject, insulin_check = EXCLUDED.insulin_check, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row("D-001", "PR-24H", "Baseline", 10, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-002", "PR-24H", "Baseline", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-003", "PR-24H", "Baseline", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-004", "PR-24H", "Baseline", 10, true, true, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-005", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-006", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-007", "PR-24H", "Phase 1", 10, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-008", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-009", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-010", "PR-24H", "Phase 1", 10, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-011", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-012", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-013", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-014", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-015", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-016", "PR-24H", "Phase 1", 10, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-017", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-018", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-019", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-020", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-021", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-022", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-023", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-024", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-025", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-026", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-027", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-028", "PR-24H", "Phase 1", 10, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-029", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-030", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-031", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-032", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-033", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-034", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-035", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-036", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-037", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-038", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-039", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-040", "PR-24H", "Phase 1", 10, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-041", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-042", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-043", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-044", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-045", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-046", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-047", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-048", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-049", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-050", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-051", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-052", "PR-24H", "Phase 1", 10, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-053", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-054", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-055", "PR-24H", "Phase 2", 20, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-056", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-057", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-058", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-059", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-060", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-061", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-062", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-063", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-064", "PR-24H", "Phase 2", 20, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-065", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-066", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-067", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-068", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-069", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-070", "PR-24H", "Phase 2", 20, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-071", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-072", "PR-24H", "Phase 3", 30, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-073", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-074", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-075", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-076", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-077", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-078", "PR-24H", "Phase 3", 30, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-079", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-080", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-081", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-082", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-083", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-084", "PR-24H", "Phase 3", 30, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-085", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-086", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-087", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-088", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-089", "PR-24H", "Phase 3", 30, true, false, false, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE"),
                row("D-090", "PR-24H", "Phase 3", 30, true, false, true, ts("2026-05-21 7:10:00"), 1, ts("2026-05-21 7:10:00"), 1, null, null, "ACTIVE")
        );
    }
    private void syncSequences() {
        jdbcTemplate.execute("SELECT setval('role_id_seq', COALESCE((SELECT MAX(role_id) FROM roles), 0) + 1, false)");
        jdbcTemplate.execute("SELECT setval('user_id_seq', COALESCE((SELECT MAX(user_id) FROM users), 0) + 1, false)");
        jdbcTemplate.execute("SELECT setval('device_id_seq', COALESCE((SELECT MAX(device_id) FROM devices), 0) + 1, false)");
    }

    private void batch(String sql, Object[]... rows) {
        jdbcTemplate.batchUpdate(sql, Arrays.asList(rows));
    }

    private static Object[] row(Object... values) {
        return values;
    }

    private static Timestamp ts(String value) {
        return Timestamp.valueOf(LocalDateTime.parse(value, DATE_TIME_FORMATTER));
    }

    private static LocalDate d(String value) {
        return LocalDate.parse(value);
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}