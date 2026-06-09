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
        seedParticipants();
        seedProtocols();
        seedSessions();
        seedActivities();
        seedDevices();
        seedProtocolDetails();
        seedBloodSamples();
        seedLabResults();
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
        "INSERT INTO sessions (session_id, participant_id, protocol_id, visit_date, start_time, end_time, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (session_id) DO UPDATE SET participant_id = EXCLUDED.participant_id, protocol_id = EXCLUDED.protocol_id, visit_date = EXCLUDED.visit_date, start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
        row(101, "PAT-001", "PR-24H", ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
    );
}

    private void seedActivities() {
        batch(
                "INSERT INTO activities (activity_id, session_id, actor_id, time, activity_type, activity_desc, activity_status, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (activity_id) DO UPDATE SET session_id = EXCLUDED.session_id, actor_id = EXCLUDED.actor_id, time = EXCLUDED.time, activity_type = EXCLUDED.activity_type, activity_desc = EXCLUDED.activity_desc, activity_status = EXCLUDED.activity_status, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row("ACT-001-T-30-101", 101, 3, ts("2026-05-21 08:00:00"), "BLOOD_DRAW", "T-30", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-002-PK-C 1-101", 101, 3, ts("2026-05-21 08:00:00"), "INSULIN_CHECK", "PK-C 1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-003-T-20-101", 101, 3, ts("2026-05-21 08:10:00"), "BLOOD_DRAW", "T-20", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-004-T-10-101", 101, 3, ts("2026-05-21 08:20:00"), "BLOOD_DRAW", "T-10", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-005-T-0-101", 101, 3, ts("2026-05-21 08:30:00"), "BLOOD_DRAW", "T-0", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-006-SC-101", 101, 3, ts("2026-05-21 08:30:00"), "INSULIN_INJECTION", "SC", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-007-GD1-101", 101, 3, ts("2026-05-21 08:40:00"), "BLOOD_DRAW", "GD1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-008-GD2-101", 101, 3, ts("2026-05-21 08:50:00"), "BLOOD_DRAW", "GD2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-009-GD3-101", 101, 3, ts("2026-05-21 09:00:00"), "BLOOD_DRAW", "GD3", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-010-PK-C 2-101", 101, 3, ts("2026-05-21 09:00:00"), "INSULIN_CHECK", "PK-C 2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-001", 101, 3, ts("2026-05-21 08:00:00"), "BLOOD_DRAW", "T-30", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-002", 101, 3, ts("2026-05-21 08:00:00"), "INSULIN_CHECK", "PK-C 1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-003", 101, 3, ts("2026-05-21 08:10:00"), "BLOOD_DRAW", "T-20", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-004", 101, 3, ts("2026-05-21 08:20:00"), "BLOOD_DRAW", "T-10", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-005", 101, 3, ts("2026-05-21 08:30:00"), "BLOOD_DRAW", "T-0", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-006", 101, 3, ts("2026-05-21 08:30:00"), "INSULIN_INJECTION", "SC", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-007", 101, 3, ts("2026-05-21 08:40:00"), "BLOOD_DRAW", "GD1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-008", 101, 3, ts("2026-05-21 08:50:00"), "BLOOD_DRAW", "GD2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-009", 101, 3, ts("2026-05-21 09:00:00"), "BLOOD_DRAW", "GD3", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-010", 101, 3, ts("2026-05-21 09:00:00"), "INSULIN_CHECK", "PK-C 2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-011", 101, 3, ts("2026-05-21 09:10:00"), "BLOOD_DRAW", "GD4", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-012", 101, 3, ts("2026-05-21 09:20:00"), "BLOOD_DRAW", "GD5", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
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

    private void seedParticipants() {
        batch(
                "INSERT INTO participants (participant_id, medical_record_no, name, gender, dob, number_phone, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (participant_id) DO UPDATE SET medical_record_no = EXCLUDED.medical_record_no, name = EXCLUDED.name, gender = EXCLUDED.gender, dob = EXCLUDED.dob, number_phone = EXCLUDED.number_phone, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
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
                "INSERT INTO protocol_details (protocol_detail_id, protocol_id, phase_code, time_interval, blood_raw, insulin_inject, insulin_check, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (protocol_detail_id) DO UPDATE SET protocol_id = EXCLUDED.protocol_id, phase_code = EXCLUDED.phase_code, time_interval = EXCLUDED.time_interval, blood_raw = EXCLUDED.blood_raw, insulin_inject = EXCLUDED.insulin_inject, insulin_check = EXCLUDED.insulin_check, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
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



    private void seedBloodSamples() {
        batch(
            "INSERT INTO blood_samples (blood_sample_id, activity_id, collected_by, sample_time, sample_type, tube_type, volume_ml, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (blood_sample_id) DO UPDATE SET activity_id = EXCLUDED.activity_id, collected_by = EXCLUDED.collected_by, sample_time = EXCLUDED.sample_time, sample_type = EXCLUDED.sample_type, tube_type = EXCLUDED.tube_type, volume_ml = EXCLUDED.volume_ml, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
            row("BS-001", "ACT-001", 4, ts("2026-05-21 08:00:05"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-002", "ACT-002", 4, ts("2026-05-21 08:00:05"), "C-Peptide", "EDTA", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-003", "ACT-003", 4, ts("2026-05-21 08:10:02"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-004", "ACT-004", 4, ts("2026-05-21 08:20:10"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-005", "ACT-005", 4, ts("2026-05-21 08:30:10"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-006", "ACT-007", 4, ts("2026-05-21 08:40:05"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-007", "ACT-008", 4, ts("2026-05-21 08:50:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-008", "ACT-009", 4, ts("2026-05-21 09:00:15"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-009", "ACT-010", 4, ts("2026-05-21 09:00:15"), "C-Peptide", "EDTA", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-010", "ACT-011", 4, ts("2026-05-21 09:10:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-011", "ACT-012", 4, ts("2026-05-21 09:20:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("BS-012", "ACT-012", 4, ts("2026-05-21 09:20:00"), "Glucose", "Fluoride", bd("3"), ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE")
        );
    }

    private void seedLabResults() {
        batch(
            "INSERT INTO lab_results (lab_result_id, blood_sample_id, parameter_name, verified_by, value, reference_range_min, reference_range_max, unit, abnormal_flag, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (lab_result_id) DO UPDATE SET blood_sample_id = EXCLUDED.blood_sample_id, parameter_name = EXCLUDED.parameter_name, verified_by = EXCLUDED.verified_by, value = EXCLUDED.value, reference_range_min = EXCLUDED.reference_range_min, reference_range_max = EXCLUDED.reference_range_max, unit = EXCLUDED.unit, abnormal_flag = EXCLUDED.abnormal_flag, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
            row("LAB-001", "BS-001", "Glucose", 4, bd("95"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-002", "BS-002", "Insulin", 4, bd("12.1"), bd("2.6"), bd("24.9"), "ulU/ml", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-003", "BS-003", "C-Peptide", 4, bd("1.5"), bd("0.5"), bd("2.7"), "ng/mL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-004", "BS-004", "Glucose", 4, bd("94"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-005", "BS-005", "Glucose", 4, bd("95.5"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-006", "BS-006", "Glucose", 4, bd("95"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-007", "BS-007", "Glucose", 4, bd("88"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-008", "BS-008", "Glucose", 4, bd("85"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-009", "BS-009", "Glucose", 4, bd("82.5"), bd("90"), bd("100"), "mg/dL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-010", "BS-010", "Insulin", 4, bd("12.1"), bd("2.6"), bd("24.9"), "ulU/ml", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE"),
            row("LAB-011", "BS-011", "C-Peptide", 4, bd("4.8"), bd("0.5"), bd("2.7"), "ng/mL", "NORMAL", ts("2026-05-21 07:10:00"), 4, ts("2026-05-21 07:10:00"), 4, null, null, "ACTIVE")
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