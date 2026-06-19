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
        seedAccessMenus();
        seedRoles();
        seedRoleAccess();
        seedUsers();
        seedParticipants();
        seedProtocols();
        seedPhaseConfigurations();
        // seedSessions();
        // seedActivities();
        // seedDevices();
        seedSamplingSchedules();
        // seedBloodSamples();
        // seedLabResults();
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

    private void seedAccessMenus() {
        batch(
            "INSERT INTO access_menus (menu_id, menu_name, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (menu_id) DO UPDATE SET menu_name = EXCLUDED.menu_name, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, status = EXCLUDED.status",
            row(1,  "USER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(2,  "ROLE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(3,  "PARTICIPANT", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(4,  "PROTOCOL", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(5,  "PHASECONFIGURATION", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(6,  "SAMPLINGSCHEDULE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(7,  "SESSION", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(8,  "SESSIONDEVICE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(9,  "INFUSIONMONITORING", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(10, "LABRESULT", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(11, "BLOODSAMPLE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(12, "VITALSIGN", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(13, "ANTHROPOMETRY", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(14, "ANAMNESIS", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(15, "DEVICE", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(16, "GLOBALCONFIGURATION", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
            row(17, "ACTIVITY", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }
    private void seedRoleAccess() {
    batch(
            "INSERT INTO role_access (role_access_id, role_id, menu_id, can_view, can_add, can_edit, can_delete, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (role_access_id) DO UPDATE SET role_id = EXCLUDED.role_id, menu_id = EXCLUDED.menu_id, can_view = EXCLUDED.can_view, can_add = EXCLUDED.can_add, can_edit = EXCLUDED.can_edit, can_delete = EXCLUDED.can_delete, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",

            row(1,  1,  1,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // USER
            row(2,  1,  2,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ROLE
            row(3,  1,  3,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PARTICIPANT
            row(4,  1,  4,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PROTOCOL
            row(5,  1,  5,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // PHASECONFIGURATION
            row(6,  1,  6,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SAMPLINGSCHEDULE
            row(7,  1,  7,  true, true, true, false,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSION
            row(8,  1,  8,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // SESSIONDEVICE
            row(9,  1,  9,  true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // INFUSIONMONITORING
            row(10, 1, 10, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // LABRESULT
            row(11, 1, 11, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // BLOODSAMPLE
            row(12, 1, 12, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // VITALSIGN
            row(13, 1, 13, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANTHROPOMETRY
            row(14, 1, 14, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // ANAMNESIS
            row(15, 1, 15, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // DEVICE
            row(16, 1, 16, true, true, true, true,  ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"), // GLOBALCONFIGURATION
            row(17, 1, 17, true, true, true, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE") // ACTIVITY
        );
    }

    private void seedUsers() {
        String password = passwordEncoder.encode("hash123");
        batch(
                "INSERT INTO users (user_id, role_id, position_name, name, username, email, password, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET role_id = EXCLUDED.role_id, position_name = EXCLUDED.position_name, name = EXCLUDED.name, username = EXCLUDED.username, email = EXCLUDED.email, password = EXCLUDED.password, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, 1, "SUPER ADMIN", "Super Admin", "superadmin", "superadmin@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, 2, "ADMINISTRASI", "Admin RSCM", "admin", "admin@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, 3, "DOKTER PENELITI", "Dr Budi", "budi", "budi@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, 4, "PERAWAT/DOKTER", "Fitri", "fitri", "fitri@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, 5, "ANALIS", "Rina", "rina", "rina@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, 6, "PERAWAT", "Agus", "agus", "agus@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, 6, "PERAWAT", "Bagas", "bagas", "bagas@mail.com", "PASSWORD_PLACEHOLDER", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "INACTIVE")
        );
        jdbcTemplate.update("UPDATE users SET password = ? WHERE password = ?", password, "PASSWORD_PLACEHOLDER");
    }

    private void seedSessions() {
    batch(
        "INSERT INTO sessions (session_id, participant_id, protocol_id, visit_date, start_time, end_time, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (session_id) DO UPDATE SET participant_id = EXCLUDED.participant_id, protocol_id = EXCLUDED.protocol_id, visit_date = EXCLUDED.visit_date, start_time = EXCLUDED.start_time, end_time = EXCLUDED.end_time, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
        row(101, "PAT-001", 1, ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
    );
}

    private void seedActivities() {
        batch(
                "INSERT INTO activities (activity_id, session_id, actor_id, time, activity_type, activity_desc, activity_status, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (activity_id) DO UPDATE SET session_id = EXCLUDED.session_id, actor_id = EXCLUDED.actor_id, time = EXCLUDED.time, activity_type = EXCLUDED.activity_type, activity_desc = EXCLUDED.activity_desc, activity_status = EXCLUDED.activity_status, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row("ACT-001-T-30-101", 101, 3, ts("2026-05-21 08:00:00"), "BLOOD_DRAW", "T-30", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-002-PK-C 1-101", 101, 3, ts("2026-05-21 08:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-003-T-20-101", 101, 3, ts("2026-05-21 08:10:00"), "BLOOD_DRAW", "T-20", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-004-T-10-101", 101, 3, ts("2026-05-21 08:20:00"), "BLOOD_DRAW", "T-10", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-005-T-0-101", 101, 3, ts("2026-05-21 08:30:00"), "BLOOD_DRAW", "T-0", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-006-SC-101", 101, 3, ts("2026-05-21 08:30:00"), "INSULIN_INJECTION", "SC", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-007-GD1-101", 101, 3, ts("2026-05-21 08:40:00"), "BLOOD_DRAW", "GD1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-008-GD2-101", 101, 3, ts("2026-05-21 08:50:00"), "BLOOD_DRAW", "GD2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-009-GD3-101", 101, 3, ts("2026-05-21 09:00:00"), "BLOOD_DRAW", "GD3", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-010-PK-C 2-101", 101, 3, ts("2026-05-21 09:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-001", 101, 3, ts("2026-05-21 08:00:00"), "BLOOD_DRAW", "T-30", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-002", 101, 3, ts("2026-05-21 08:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-003", 101, 3, ts("2026-05-21 08:10:00"), "BLOOD_DRAW", "T-20", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-004", 101, 3, ts("2026-05-21 08:20:00"), "BLOOD_DRAW", "T-10", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-005", 101, 3, ts("2026-05-21 08:30:00"), "BLOOD_DRAW", "T-0", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-006", 101, 3, ts("2026-05-21 08:30:00"), "INSULIN_INJECTION", "SC", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-007", 101, 3, ts("2026-05-21 08:40:00"), "BLOOD_DRAW", "GD1", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-008", 101, 3, ts("2026-05-21 08:50:00"), "BLOOD_DRAW", "GD2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-009", 101, 3, ts("2026-05-21 09:00:00"), "BLOOD_DRAW", "GD3", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("ACT-010", 101, 3, ts("2026-05-21 09:00:00"), "PK_SAMPLE_COLLECTION", "PK-C 2", "COMPLETED", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
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
            "INSERT INTO protocols (" +
            "protocol_code, protocol_name, " +
            "insulin_dose_rule, insulin_dose_unit, " +
            "glucose_target_min, glucose_target_max, " +
            "glucose_target_min_extreme, glucose_target_max_extreme, " +
            "glucose_target_unit, duration_hours, version, " +
            "created_at, created_by, updated_at, updated_by, " +
            "deleted_at, deleted_by, status" +
            ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (protocol_code) DO UPDATE SET " +
            "protocol_code = EXCLUDED.protocol_code, " +
            "protocol_name = EXCLUDED.protocol_name, " +
            "insulin_dose_rule = EXCLUDED.insulin_dose_rule, " +
            "insulin_dose_unit = EXCLUDED.insulin_dose_unit, " +
            "glucose_target_min = EXCLUDED.glucose_target_min, " +
            "glucose_target_max = EXCLUDED.glucose_target_max, " +
            "glucose_target_min_extreme = EXCLUDED.glucose_target_min_extreme, " +
            "glucose_target_max_extreme = EXCLUDED.glucose_target_max_extreme, " +
            "glucose_target_unit = EXCLUDED.glucose_target_unit, " +
            "duration_hours = EXCLUDED.duration_hours, " +
            "version = EXCLUDED.version, " +
            "created_at = EXCLUDED.created_at, " +
            "created_by = EXCLUDED.created_by, " +
            "updated_at = EXCLUDED.updated_at, " +
            "updated_by = EXCLUDED.updated_by, " +
            "deleted_at = EXCLUDED.deleted_at, " +
            "deleted_by = EXCLUDED.deleted_by, " +
            "status = EXCLUDED.status",

            row(
                "EGC001",
                "Euglycemic Clamp",
                "0.5",
                "U/kgBW SC",
                bd("90"),      // glucose_target_min
                bd("100"),     // glucose_target_max
                bd("80"),      // glucose_target_min_extreme
                bd("110"),     // glucose_target_max_extreme
                "mg/dL",
                bd("24"),
                1.0f,
                ts("2026-05-21 07:10:00"),
                1,
                ts("2026-05-21 07:10:00"),
                1,
                null,
                null,
                "ACTIVE"
            )
        );
    }

    private void seedPhaseConfigurations() {
        batch(
                "INSERT INTO phase_configurations (phase_conf_id, phase_conf_priority, phase_conf_code, phase_conf_name, phase_conf_type, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (phase_conf_id) DO UPDATE SET phase_conf_priority = EXCLUDED.phase_conf_priority, phase_conf_code = EXCLUDED.phase_conf_code, phase_conf_name = EXCLUDED.phase_conf_name, phase_conf_type = EXCLUDED.phase_conf_type, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                row(1, 1, "PREP1", "Pemeriksaan Awal", "preparation", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(2, 2, "PREP2", "Pra - Tindakan", "preparation", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(3, 3, "BASE", "Baseline", "pre-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(4, 4, "PH1", "Phase 1", "post-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(5, 5, "PH2", "Phase 2", "post-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(6, 6, "PH3", "Phase 3", "post-insulin", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row(7, 7, "FINAL", "Pemeriksaan Akhir", "finalization", ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
        );
    }

    private void seedSamplingSchedules() {
        batch(
                "INSERT INTO sampling_schedules (sampling_schedule_id, protocol_id, phase_code, phase_name, phase_type, relative_minute, time_interval, blood_raw, insulin_inject, pk_sample_collection, created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (sampling_schedule_id) DO UPDATE SET " +
                "protocol_id = EXCLUDED.protocol_id, phase_code = EXCLUDED.phase_code, phase_name = EXCLUDED.phase_name, phase_type = EXCLUDED.phase_type, " +
                "relative_minute = EXCLUDED.relative_minute, time_interval = EXCLUDED.time_interval, blood_raw = EXCLUDED.blood_raw, insulin_inject = EXCLUDED.insulin_inject, " +
                "pk_sample_collection = EXCLUDED.pk_sample_collection, created_at = EXCLUDED.created_at, created_by = EXCLUDED.created_by, " +
                "updated_at = EXCLUDED.updated_at, updated_by = EXCLUDED.updated_by, deleted_at = EXCLUDED.deleted_at, deleted_by = EXCLUDED.deleted_by, status = EXCLUDED.status",
                
                // PREPARATION (D-001 menit 0, D-002 menit 30)
                row("D-001", 1, "PREP1", "Pemeriksaan Awal", "preparation", 0, 30, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-002", 1, "PREP2", "Pra - Tindakan", "preparation", 30, 30, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                
                // BASELINE (Langkah interval masing-masing 10m dimulai dari menit ke-60)
                row("D-003", 1, "BASE", "Baseline", "pre-insulin", 60, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-004", 1, "BASE", "Baseline", "pre-insulin", 70, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-005", 1, "BASE", "Baseline", "pre-insulin", 80, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-006", 1, "BASE", "Baseline", "pre-insulin", 90, 10, true, true, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),

                // PHASE 1 (GD1 s.d GD48 - Interval 10m, dimulai dari menit ke-100 s.d 570)
                row("D-007", 1, "PH1", "Phase 1", "post-insulin", 100, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-008", 1, "PH1", "Phase 1", "post-insulin", 110, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-009", 1, "PH1", "Phase 1", "post-insulin", 120, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-010", 1, "PH1", "Phase 1", "post-insulin", 130, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-011", 1, "PH1", "Phase 1", "post-insulin", 140, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-012", 1, "PH1", "Phase 1", "post-insulin", 150, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-013", 1, "PH1", "Phase 1", "post-insulin", 160, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-014", 1, "PH1", "Phase 1", "post-insulin", 170, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-015", 1, "PH1", "Phase 1", "post-insulin", 180, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-016", 1, "PH1", "Phase 1", "post-insulin", 190, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-017", 1, "PH1", "Phase 1", "post-insulin", 200, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-018", 1, "PH1", "Phase 1", "post-insulin", 210, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-019", 1, "PH1", "Phase 1", "post-insulin", 220, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-020", 1, "PH1", "Phase 1", "post-insulin", 230, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-021", 1, "PH1", "Phase 1", "post-insulin", 240, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-022", 1, "PH1", "Phase 1", "post-insulin", 250, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-023", 1, "PH1", "Phase 1", "post-insulin", 260, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-024", 1, "PH1", "Phase 1", "post-insulin", 270, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-025", 1, "PH1", "Phase 1", "post-insulin", 280, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-026", 1, "PH1", "Phase 1", "post-insulin", 290, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-027", 1, "PH1", "Phase 1", "post-insulin", 300, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-028", 1, "PH1", "Phase 1", "post-insulin", 310, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-029", 1, "PH1", "Phase 1", "post-insulin", 320, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-030", 1, "PH1", "Phase 1", "post-insulin", 330, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-031", 1, "PH1", "Phase 1", "post-insulin", 340, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-032", 1, "PH1", "Phase 1", "post-insulin", 350, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-033", 1, "PH1", "Phase 1", "post-insulin", 360, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-034", 1, "PH1", "Phase 1", "post-insulin", 370, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-035", 1, "PH1", "Phase 1", "post-insulin", 380, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-036", 1, "PH1", "Phase 1", "post-insulin", 390, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-037", 1, "PH1", "Phase 1", "post-insulin", 400, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-038", 1, "PH1", "Phase 1", "post-insulin", 410, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-039", 1, "PH1", "Phase 1", "post-insulin", 420, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-040", 1, "PH1", "Phase 1", "post-insulin", 430, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-041", 1, "PH1", "Phase 1", "post-insulin", 440, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-042", 1, "PH1", "Phase 1", "post-insulin", 450, 10, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-043", 1, "PH1", "Phase 1", "post-insulin", 460, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-044", 1, "PH1", "Phase 1", "post-insulin", 470, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-045", 1, "PH1", "Phase 1", "post-insulin", 480, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-046", 1, "PH1", "Phase 1", "post-insulin", 490, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-047", 1, "PH1", "Phase 1", "post-insulin", 500, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-048", 1, "PH1", "Phase 1", "post-insulin", 510, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-049", 1, "PH1", "Phase 1", "post-insulin", 520, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-050", 1, "PH1", "Phase 1", "post-insulin", 530, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-051", 1, "PH1", "Phase 1", "post-insulin", 540, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-052", 1, "PH1", "Phase 1", "post-insulin", 550, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-053", 1, "PH1", "Phase 1", "post-insulin", 560, 10, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-054", 1, "PH1", "Phase 1", "post-insulin", 570, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),

                // PHASE 2 (GD49 s.d GD66 - tipe 'post-insulin' PH2, dimulai dari menit ke-590 s.d 930)
                row("D-055", 1, "PH2", "Phase 2", "post-insulin", 590, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-056", 1, "PH2", "Phase 2", "post-insulin", 610, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-057", 1, "PH2", "Phase 2", "post-insulin", 630, 20, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-058", 1, "PH2", "Phase 2", "post-insulin", 650, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-059", 1, "PH2", "Phase 2", "post-insulin", 670, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-060", 1, "PH2", "Phase 2", "post-insulin", 690, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-061", 1, "PH2", "Phase 2", "post-insulin", 710, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-062", 1, "PH2", "Phase 2", "post-insulin", 730, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-063", 1, "PH2", "Phase 2", "post-insulin", 750, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-064", 1, "PH2", "Phase 2", "post-insulin", 770, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-065", 1, "PH2", "Phase 2", "post-insulin", 790, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-066", 1, "PH2", "Phase 2", "post-insulin", 810, 20, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-067", 1, "PH2", "Phase 2", "post-insulin", 830, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-068", 1, "PH2", "Phase 2", "post-insulin", 850, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-069", 1, "PH2", "Phase 2", "post-insulin", 870, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-070", 1, "PH2", "Phase 2", "post-insulin", 890, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-071", 1, "PH2", "Phase 2", "post-insulin", 910, 20, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-072", 1, "PH2", "Phase 2", "post-insulin", 930, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),

                // PHASE 3 (GD67 s.d GD86 - tipe 'post-insulin' PH3, dimulai dari menit ke-960 s.d 1530)
                row("D-073", 1, "PH3", "Phase 3", "post-insulin", 960, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-074", 1, "PH3", "Phase 3", "post-insulin", 990, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-075", 1, "PH3", "Phase 3", "post-insulin", 1020, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-076", 1, "PH3", "Phase 3", "post-insulin", 1050, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-077", 1, "PH3", "Phase 3", "post-insulin", 1080, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-078", 1, "PH3", "Phase 3", "post-insulin", 1110, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-079", 1, "PH3", "Phase 3", "post-insulin", 1140, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-080", 1, "PH3", "Phase 3", "post-insulin", 1170, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-081", 1, "PH3", "Phase 3", "post-insulin", 1200, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-082", 1, "PH3", "Phase 3", "post-insulin", 1230, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-083", 1, "PH3", "Phase 3", "post-insulin", 1260, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-084", 1, "PH3", "Phase 3", "post-insulin", 1290, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-085", 1, "PH3", "Phase 3", "post-insulin", 1320, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-086", 1, "PH3", "Phase 3", "post-insulin", 1350, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-087", 1, "PH3", "Phase 3", "post-insulin", 1380, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-088", 1, "PH3", "Phase 3", "post-insulin", 1410, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-089", 1, "PH3", "Phase 3", "post-insulin", 1440, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-090", 1, "PH3", "Phase 3", "post-insulin", 1470, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-091", 1, "PH3", "Phase 3", "post-insulin", 1500, 30, true, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),
                row("D-092", 1, "PH3", "Phase 3", "post-insulin", 1530, 30, true, false, true, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE"),

                // FINALIZATION (FINAL - menit 1530)
                row("D-093", 1, "FINAL", "Pemeriksaan Akhir", "finalization", 1530, 30, false, false, false, ts("2026-05-21 07:10:00"), 1, ts("2026-05-21 07:10:00"), 1, null, null, "ACTIVE")
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
        jdbcTemplate.execute("SELECT setval('phase_conf_id_seq', COALESCE((SELECT MAX(phase_conf_id) FROM phase_configurations), 0) + 1, false)");
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