package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.SessionCreateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.SessionCompleteRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.SessionUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.MessageResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionCreateResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionSummaryResponse;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.SamplingSchedule;
import com.tujuhsembilan.glucoseclamp.model.Device;
import com.tujuhsembilan.glucoseclamp.model.Participant;
import com.tujuhsembilan.glucoseclamp.model.Protocol;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.SessionDevice;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import com.tujuhsembilan.glucoseclamp.repository.AnamnesisRepository;
import com.tujuhsembilan.glucoseclamp.repository.AnthropometryRepository;
import com.tujuhsembilan.glucoseclamp.repository.DeviceRepository;
import com.tujuhsembilan.glucoseclamp.repository.ParticipantRepository;
import com.tujuhsembilan.glucoseclamp.repository.ProtocolRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionDeviceRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.repository.VitalSignRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
import com.tujuhsembilan.glucoseclamp.security.service.CurrentUserService;
// Removed Lombok constructor annotation to avoid IDE/compiler mismatch issues

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionManagementService {

    private final SessionRepository sessionRepository;
    private final ParticipantRepository participantRepository;
    private final ProtocolRepository protocolRepository;
    private final DeviceRepository deviceRepository;
    private final SessionDeviceRepository sessionDeviceRepository;
    private final VitalSignRepository vitalSignRepository;
    private final AnamnesisRepository anamnesisRepository;
    private final AnthropometryRepository anthropometryRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final ActivityService activityService;

    public SessionManagementService(
            SessionRepository sessionRepository,
            ParticipantRepository participantRepository,
            ProtocolRepository protocolRepository,
            DeviceRepository deviceRepository,
            SessionDeviceRepository sessionDeviceRepository,
            VitalSignRepository vitalSignRepository,
            AnamnesisRepository anamnesisRepository,
            AnthropometryRepository anthropometryRepository,
            UserRepository userRepository,
                CurrentUserService currentUserService,
                ActivityService activityService
    ) {
        this.sessionRepository = sessionRepository;
        this.participantRepository = participantRepository;
        this.protocolRepository = protocolRepository;
        this.deviceRepository = deviceRepository;
        this.sessionDeviceRepository = sessionDeviceRepository;
        this.vitalSignRepository = vitalSignRepository;
        this.anamnesisRepository = anamnesisRepository;
        this.anthropometryRepository = anthropometryRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.activityService = activityService;
    }

    public ApiDataResponseBuilder getAllSessions(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<SessionSummaryResponse> result = sessionRepository.findAllSessionSummaries(pageable);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data sesi")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse startSession(Integer sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findByIdAndDeletedAtIsNull(sessionId);
        if (sessionOptional.isEmpty()) {
            return new MessageResponse("Data session tidak ditemukan", HttpStatus.NOT_FOUND.value(), "NOT_FOUND");
        }

        Session session = sessionOptional.get();
        if (session.getSessionStatus() != SessionStatus.PREP) {
            return new MessageResponse("Session ini tidak bisa dimulai", HttpStatus.CONFLICT.value(), "CONFLICT");
        }

        session.setSessionStatus(SessionStatus.RUNNING);
        session.setUpdatedBy(currentUserService.getCurrentUserId());
        sessionRepository.save(session);

        return new MessageResponse("Session berhasil dimulai", HttpStatus.OK.value(), "OK");
    }

    @Transactional
    public ApiDataResponseBuilder create(SessionCreateRequest request) {
        Integer actorId = currentUserService.getCurrentUserId();
        User actor = currentUserService.getCurrentUserEntity();

        Optional<Participant> participantOptional = participantRepository.findByIdAndDeletedAtIsNull(request.getParticipantId());
        if (participantOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Participant tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Protocol> protocolOptional = protocolRepository.findByIdAndDeletedAtIsNull(request.getProtocolId());
        if (protocolOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
        //     return ApiDataResponseBuilder.builder()
        //             .message("Device minimal 1 harus diisi")
        //             .statusCode(HttpStatus.BAD_REQUEST.value())
        //             .status(HttpStatus.BAD_REQUEST)
        //             .build();
        // }

        // List<Device> devices = new ArrayList<>();
        // for (Integer deviceId : request.getDeviceIds()) {
        //     Optional<Device> deviceOptional = deviceRepository.findByIdAndDeletedAtIsNull(deviceId);
        //     if (deviceOptional.isEmpty()) {
        //         return ApiDataResponseBuilder.builder()
        //                 .message("Device tidak ditemukan: " + deviceId)
        //                 .statusCode(HttpStatus.BAD_REQUEST.value())
        //                 .status(HttpStatus.BAD_REQUEST)
        //                 .build();
        //     }
        //     devices.add(deviceOptional.get());
        // }

        Session session = new Session();
        session.setParticipant(participantOptional.get());
        session.setProtocol(protocolOptional.get());
        session.setVisitDate(request.getVisitDate());
        session.setStartTime(normalizeToSeconds(request.getStartTime()));
        session.setFastingHour(request.getFastingHour());
        session.setSessionStatus(SessionStatus.PREP);
        session.setCreatedBy(actorId);
        session.setUpdatedBy(actorId);
        sessionRepository.save(session);

        // List<SessionDevice> sessionDevices = new ArrayList<>();
        // for (Device device : devices) {
        //     sessionDevices.add(SessionDevice.builder()
        //             .session(session)
        //             .device(device)
        //             .assignedAt(java.time.LocalDateTime.now())
        //             .assignedByUser(actor)
        //             .build());
        // }
        // sessionDeviceRepository.saveAll(sessionDevices);

        List<Activity> activities = activityService.generateActivitiesForSession(session, actor, actorId);
        activityService.saveActivities(activities);

        LocalDateTime estimatedEndTime = activities.stream()
                .map(Activity::getTime)
                .filter(java.util.Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .map(this::normalizeToSeconds)
                .orElse(session.getStartTime());

        session.setEndTime(estimatedEndTime);
        sessionRepository.save(session);

        SessionCreateResponse response = buildSessionCreateResponse(session, activities);
        // response.setSessionDeviceIds(sessionDevices.stream()
        //         .map(SessionDevice::getSessionDeviceId)
        //         .collect(java.util.stream.Collectors.toList()));
        response.setEndTime(estimatedEndTime);

        return ApiDataResponseBuilder.builder()
                .data(response)
                .message("Session berhasil dibuat, device dan measurements berhasil disimpan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder complete(Integer sessionId, SessionCompleteRequest request) {
        Optional<Session> sessionOptional = sessionRepository.findByIdAndDeletedAtIsNull(sessionId);
        if (sessionOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Session session = sessionOptional.get();
        if (session.getSessionStatus() != SessionStatus.RUNNING) {
            return ApiDataResponseBuilder.builder()
                    .message("Session hanya bisa di-complete saat status masih RUNNING")
                    .statusCode(HttpStatus.CONFLICT.value())
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        session.setEndTime(normalizeToSeconds(request.getEndTime()));
        session.setSessionStatus(SessionStatus.COMPLETED);
        session.setEndReasonCategory(request.getEndReasonCategory());
        session.setEndReasonDetail(request.getEndReasonDetail());
        session.setUpdatedBy(currentUserService.getCurrentUserId());
        sessionRepository.save(session);

        return ApiDataResponseBuilder.builder()
                .data(buildSessionCompleteResponse(session))
                .message("Session berhasil di-complete")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder update(Integer sessionId, SessionUpdateRequest request) {
        Integer actorId = currentUserService.getCurrentUserId();
        User actor = currentUserService.getCurrentUserEntity();

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId).orElse(null);
        if (session == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (session.getSessionStatus() != SessionStatus.PREP) {
            return ApiDataResponseBuilder.builder()
                    .message("Session hanya bisa diedit saat status masih PREP")
                    .statusCode(HttpStatus.CONFLICT.value())
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        Optional<Protocol> protocolOptional = protocolRepository.findByIdAndDeletedAtIsNull(request.getProtocolId());
        if (protocolOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        boolean protocolChanged = !session.getProtocol().getProtocolId().equals(request.getProtocolId());
        session.setProtocol(protocolOptional.get());
        session.setVisitDate(request.getVisitDate());
        LocalDateTime oldStart = normalizeToSeconds(session.getStartTime());
        LocalDateTime newStart = normalizeToSeconds(request.getStartTime());
        session.setFastingHour(request.getFastingHour());
        session.setUpdatedBy(actorId);

        if (protocolChanged) {
            session.setStartTime(newStart);
            session.setEndTime(normalizeToSeconds(request.getEndTime()));
            sessionRepository.save(session);

            activityService.softDeleteActivitiesForSession(sessionId, actorId);
            List<Activity> activities = activityService.generateActivitiesForSession(session, actor, actorId);
            activityService.saveActivities(activities);

            return ApiDataResponseBuilder.builder()
                    .data(buildSessionCreateResponse(session, activities))
                    .message("Session berhasil diperbarui dan activity berhasil diregenerate karena perubahan protocol")
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .build();
        }

        if (newStart != null && !newStart.equals(oldStart)) {
            Duration delta = Duration.between(oldStart, newStart);
            List<Activity> activitiesToUpdate = activityService.shiftPendingActivitiesForSession(sessionId, delta, actorId);

            LocalDateTime estimatedEndTime = activityService.findActiveActivitiesForSession(sessionId).stream()
                    .map(Activity::getTime)
                    .filter(java.util.Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .map(this::normalizeToSeconds)
                    .orElse(newStart);

            session.setStartTime(newStart);
            session.setEndTime(estimatedEndTime);
            sessionRepository.save(session);

            return ApiDataResponseBuilder.builder()
                    .data(buildSessionCreateResponse(session, activitiesToUpdate))
                    .message("Session berhasil diperbarui dan aktivitas digeser sesuai startTime baru")
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .build();
        }

        session.setStartTime(newStart);
        session.setEndTime(normalizeToSeconds(request.getEndTime()));
        sessionRepository.save(session);

        return ApiDataResponseBuilder.builder()
                .data(buildSessionCreateResponse(session, new ArrayList<>()))
                .message("Session berhasil diperbarui")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateSessionStatus(Integer sessionId, com.tujuhsembilan.glucoseclamp.dto.request.SessionStatusUpdateRequest request) {
        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId).orElse(null);
        if (session == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = currentUserService.getCurrentUserId();

        session.setStatus(request.getStatus());
        session.setUpdatedBy(currentUserId);
        sessionRepository.save(session);

        // Cascade status to relationships
        activityService.updateActivityStatusForSession(sessionId, request.getStatus(), currentUserId);

        List<SessionDevice> devices = sessionDeviceRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        devices.forEach(d -> { d.setStatus(request.getStatus()); d.setUpdatedBy(currentUserId); });
        sessionDeviceRepository.saveAll(devices);

        List<com.tujuhsembilan.glucoseclamp.model.VitalSign> vitals = vitalSignRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        vitals.forEach(v -> { v.setStatus(request.getStatus()); v.setUpdatedBy(currentUserId); });
        vitalSignRepository.saveAll(vitals);

        anamnesisRepository.findBySessionIdAndDeletedAtIsNull(sessionId).ifPresent(a -> {
            a.setStatus(request.getStatus());
            a.setUpdatedBy(currentUserId);
            anamnesisRepository.save(a);
        });

        anthropometryRepository.findBySessionIdAndDeletedAtIsNull(sessionId).ifPresent(a -> {
            a.setStatus(request.getStatus());
            a.setUpdatedBy(currentUserId);
            anthropometryRepository.save(a);
        });

        return ApiDataResponseBuilder.builder()
                .message("Status session dan relasinya berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteSession(Integer sessionId) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId).orElse(null);
        if (session == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = currentUserService.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        session.setDeletedAt(now);
        session.setDeletedBy(currentUserId);
        session.setStatus(EntityStatus.DELETED);
        session.setUpdatedBy(currentUserId);
        sessionRepository.save(session);

        // Cascade soft delete to relationships
        activityService.softDeleteActivitiesForSession(sessionId, currentUserId);

        List<SessionDevice> devices = sessionDeviceRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        devices.forEach(d -> {
            d.setDeletedAt(now);
            d.setDeletedBy(currentUserId);
            d.setStatus(EntityStatus.DELETED);
            d.setUpdatedBy(currentUserId);
        });
        sessionDeviceRepository.saveAll(devices);

        List<com.tujuhsembilan.glucoseclamp.model.VitalSign> vitals = vitalSignRepository.findBySessionIdAndDeletedAtIsNull(sessionId);
        vitals.forEach(v -> {
            v.setDeletedAt(now);
            v.setDeletedBy(currentUserId);
            v.setStatus(EntityStatus.DELETED);
            v.setUpdatedBy(currentUserId);
        });
        vitalSignRepository.saveAll(vitals);

        anamnesisRepository.findBySessionIdAndDeletedAtIsNull(sessionId).ifPresent(a -> {
            a.setDeletedAt(now);
            a.setDeletedBy(currentUserId);
            a.setStatus(EntityStatus.DELETED);
            a.setUpdatedBy(currentUserId);
            anamnesisRepository.save(a);
        });

        anthropometryRepository.findBySessionIdAndDeletedAtIsNull(sessionId).ifPresent(a -> {
            a.setDeletedAt(now);
            a.setDeletedBy(currentUserId);
            a.setStatus(EntityStatus.DELETED);
            a.setUpdatedBy(currentUserId);
            anthropometryRepository.save(a);
        });

        return ApiDataResponseBuilder.builder()
                .message("Session dan relasinya berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private SessionCreateResponse buildSessionCreateResponse(Session session, List<Activity> activities) {
        List<String> activityIds = activities.stream()
                .map(Activity::getActivityId)
                .collect(Collectors.toList());

        SessionCreateResponse resp = new SessionCreateResponse();
        resp.setSessionId(session.getSessionId());
        resp.setParticipantId(session.getParticipant().getParticipantId());
        resp.setProtocolId(session.getProtocol().getProtocolId());
        resp.setGeneratedActivityCount(activityIds.size());
        resp.setActivityIds(activityIds);
        resp.setEndTime(session.getEndTime());
        return resp;
    }

    private SessionCreateResponse buildSessionCompleteResponse(Session session) {
        SessionCreateResponse resp = new SessionCreateResponse();
        resp.setSessionId(session.getSessionId());
        resp.setParticipantId(session.getParticipant().getParticipantId());
        resp.setProtocolId(session.getProtocol().getProtocolId());
        resp.setEndTime(session.getEndTime());
        return resp;
    }

    private LocalDateTime parseFlexibleDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.contains("T")) {
            return normalizeToSeconds(LocalDateTime.parse(value));
        }
        return normalizeToSeconds(LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss")));
    }

    private LocalDateTime normalizeToSeconds(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.truncatedTo(ChronoUnit.SECONDS);
    }

}