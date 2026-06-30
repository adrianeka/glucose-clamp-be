package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.InfusionMonitoringResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.LabResultItemResultResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionActivityItemResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionTimelineResponse;
import com.tujuhsembilan.glucoseclamp.exception.classes.DataNotFoundException;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import com.tujuhsembilan.glucoseclamp.model.LabResult;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.repository.InfusionMonitoringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SessionTrackingService {

    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;
    private final InfusionMonitoringRepository infusionMonitoringRepository;
    private static final long TIME_TOLERANCE_SECONDS = 1;

    @Transactional(readOnly = true)
    public ApiDataResponseBuilder getTimeline(Long sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findByIdAndDeletedAtIsNull(sessionId);
        if (sessionOptional.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Session session = sessionOptional.get();
        List<Activity> activities = activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId)
                .stream()
                .sorted(Comparator.comparing(Activity::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Activity::getActivityId))
                .collect(Collectors.toList());

        List<SessionActivityItemResponse> activityResponses = activities.stream()
                .map(this::toActivityItemResponse)
                .collect(Collectors.toList());

        int completedActivities = (int) activities.stream()
                .filter(this::isCompletedActivity)
                .count();

        int totalActivities = activities.size();
        int progressPercentage = totalActivities == 0 ? 0 : (int) Math.round((completedActivities * 100.0) / totalActivities);

        List<Activity> nextPendingActivities = findNextActivities(activities);
        List<SessionActivityItemResponse> nextActivityResponses = nextPendingActivities.stream()
                .map(this::toActivityItemResponse)
                .collect(Collectors.toList());
        SessionActivityItemResponse nextActivity = nextActivityResponses.stream().findFirst().orElse(null);

        SessionStatus sessionStatus = session.getSessionStatus();
                if (sessionStatus == SessionStatus.PREP && completedActivities > 0) {
            sessionStatus = SessionStatus.RUNNING;
        }
        if (totalActivities > 0 && completedActivities == totalActivities) {
            sessionStatus = SessionStatus.COMPLETED;
        }
        List<InfusionMonitoringResponse> infusionResponses =
        infusionMonitoringRepository
                .findBySessionSessionIdAndDeletedAtIsNullOrderByTimeAsc(sessionId)
                .stream()
                .map(this::toInfusionResponse)
                .toList();

        SessionTimelineResponse response = new SessionTimelineResponse();
        response.setSessionId(session.getSessionId());
        response.setParticipantId(session.getParticipant().getParticipantId());
        response.setParticipantName(session.getParticipant().getName());
        response.setProtocolId(session.getProtocol().getProtocolId());
        response.setProtocolName(session.getProtocol().getProtocolName());
        response.setVisitDate(session.getVisitDate());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setSessionStatus(sessionStatus);
        response.setTotalActivities(totalActivities);
        response.setCompletedActivities(completedActivities);
        response.setProgressPercentage(progressPercentage);
        response.setNextActivities(nextActivityResponses);
        response.setActivities(activityResponses);
        response.setInfusion(infusionResponses);

        return ApiDataResponseBuilder.builder()
                .data(response)
                .message("Berhasil mendapatkan timeline session")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private SessionActivityItemResponse toActivityItemResponse(Activity activity) {
        List<LabResultItemResultResponse> labResults =
            Optional.ofNullable(activity.getBloodSamples())
                    .orElse(Collections.emptyList())
                    .stream()
                    .flatMap(bs -> Optional.ofNullable(bs.getLabResults())
                            .orElse(Collections.emptyList())
                            .stream())
                    .map(this::toLabResultResponse)
                    .toList();
        // return new SessionActivityItemResponse(
        //         activity.getActivityId(),
        //         activity.getTime(),
        //         activity.getActivityType(),
        //         activity.getActivityDesc(),
        //         activity.getPhaseCode(),
        //         activity.getPhaseName(),
        //         activity.getActivityStatus(),
        //         activity.getMinute(),
        //         activity.getScheduleCode(),
        //         activity.getPhaseType(),
        //         labResults
        // );
        return SessionActivityItemResponse.builder()
                .activityId(activity.getActivityId())
                .time(activity.getTime())
                .activityType(activity.getActivityType())
                .activityDesc(activity.getActivityDesc())
                .phaseCode(activity.getPhaseCode())
                .phaseName(activity.getPhaseName())
                .phaseType(activity.getPhaseType())
                .activityStatus(activity.getActivityStatus())
                .minute(activity.getMinute())
                .scheduleCode(activity.getScheduleCode())
                .labResults(labResults)
                .build();
    }

    private LabResultItemResultResponse toLabResultResponse(LabResult labResult) {
        return LabResultItemResultResponse.builder()
                .labResultId(labResult.getLabResultId().toString())
                .bloodSampleId(
                        labResult.getBloodSample() != null
                                ? labResult.getBloodSample().getBloodSampleId().toString()
                                : null
                )
                .parameterName(labResult.getParameterName())
                .value(labResult.getValue())
                .referenceRangeMin(labResult.getReferenceRangeMin())
                .referenceRangeMax(labResult.getReferenceRangeMax())
                .unit(labResult.getUnit())
                .abnormalFlag(labResult.getAbnormalFlag())
                .build();
    }

    private InfusionMonitoringResponse toInfusionResponse(
            InfusionMonitoring infusion
    ) {
        return InfusionMonitoringResponse.builder()
                .infusionId(infusion.getInfusionId())
                .time(infusion.getTime())
                .glucoseValue(infusion.getGlucoseValue())
                .actualGir(infusion.getActualGir())
                .recommendedGir(infusion.getRecommendedGir())
                .flowRateMlHr(infusion.getFlowRateMlHr())
                .adjustmentNote(infusion.getAdjustmentNote())
                .monitoredBy(infusion.getMonitoredBy())
                .build();
    }

    private boolean isCompletedActivity(Activity activity) {
                return activity != null && activity.getActivityStatus() == ActivityStatus.COMPLETED;
    }
        // private List<Activity> findNextPendingActivities(List<Activity> activities) {
        //         Optional<Activity> firstPending = activities.stream()
        //                         .filter(activity -> !isCompletedActivity(activity))
        //                         .findFirst();

        //         if (firstPending.isEmpty()) {
        //                 return List.of();
        //         }

        //         var nextTime = firstPending.get().getTime();
        //         return activities.stream()
        //                 .filter(activity -> !isCompletedActivity(activity))
        //                 .filter(activity -> Objects.equals(activity.getTime(), nextTime))
        //                 .collect(Collectors.toList());
        // }

    private List<Activity> findNextActivities(List<Activity> activities) {
        /*
        * ============================================================
        * SEMUA SUDAH SELESAI
        * ============================================================
        */
        if (activities.stream().allMatch(this::isCompletedActivity)) {
            return List.of();
        }
        /*
        * ============================================================
        * MASIH ADA ACTIVITY IN_PROGRESS
        * ============================================================
        */
        List<Activity> currentActivities = activities.stream()
                .filter(a -> a.getActivityStatus() == ActivityStatus.IN_PROGRESS)
                .toList();

        if (!currentActivities.isEmpty()) {

            LocalDateTime currentTime = currentActivities.get(0).getTime();

            Optional<LocalDateTime> nextTime = activities.stream()
                    .map(Activity::getTime)
                    .filter(Objects::nonNull)
                    .filter(time -> time.isAfter(currentTime))
                    .distinct()
                    .sorted()
                    .findFirst();

            if (nextTime.isEmpty()) {
                return List.of();
            }

            return activities.stream()
                    .filter(a -> a.getActivityStatus() != ActivityStatus.COMPLETED)
                    .filter(a -> Objects.equals(a.getTime(), nextTime.get()))
                    .toList();
        }

        /*
        * ============================================================
        * TIDAK ADA IN_PROGRESS
        * TAMPILKAN ACTIVITY INQUEUE PERTAMA
        * MESKIPUN BELUM WAKTUNYA
        * ============================================================
        */
        Optional<LocalDateTime> nextPendingTime = activities.stream()
                .filter(a -> a.getActivityStatus() == ActivityStatus.INQUEUE)
                .map(Activity::getTime)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .findFirst();

        if (nextPendingTime.isEmpty()) {
            return List.of();
        }

        return activities.stream()
                .filter(a -> a.getActivityStatus() == ActivityStatus.INQUEUE)
                .filter(a -> Objects.equals(a.getTime(), nextPendingTime.get()))
                .toList();
    }

    @Transactional
    public ApiDataResponseBuilder nextProgressActivity(Long sessionId) {

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Data session tidak ditemukan"));

        List<Activity> activities =
                activityRepository
                        .findBySessionIdAndDeletedAtIsNull(sessionId)
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        Activity::getTime,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                                .thenComparing(
                                        Activity::getActivityId
                                )
                        )
                        .toList();

        if (activities.isEmpty()) {

            return ApiDataResponseBuilder.builder()
                    .message("Activity tidak ditemukan")
                    .status(HttpStatus.NOT_FOUND)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build();

        }

        LocalDateTime now = LocalDateTime.now();

        /*
        * ==========================================================
        * MASIH ADA YANG IN_PROGRESS
        * ==========================================================
        */

        boolean hasInProgress =
                activities.stream()
                        .anyMatch(a ->
                                a.getActivityStatus()
                                        == ActivityStatus.IN_PROGRESS);

        if (hasInProgress) {

            return ApiDataResponseBuilder.builder()
                    .message("Activity saat ini masih berjalan")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();

        }

        /*
        * ==========================================================
        * CARI ACTIVITY INQUEUE PERTAMA
        * ==========================================================
        */

        Optional<LocalDateTime> nextTime =
                activities.stream()

                        .filter(a ->
                                a.getActivityStatus()
                                        == ActivityStatus.INQUEUE)
                        .map(Activity::getTime)
                        .filter(Objects::nonNull)
                        .sorted()
                        .findFirst();

        if (nextTime.isEmpty()) {

            return ApiDataResponseBuilder.builder()
                    .message("Tidak ada activity berikutnya")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();

        }

        /*
        * ==========================================================
        * BELUM WAKTUNYA DIMULAI
        * ==========================================================
        */

        if (isTooEarly(now, nextTime.get())) {

            return ApiDataResponseBuilder.builder()
                    .message("Belum waktunya memulai activity berikutnya")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();
        }

        /*
        * ==========================================================
        * PROMOTE INQUEUE -> IN_PROGRESS
        * ==========================================================
        */

        List<Activity> activitiesToStart =
                activities.stream()
                        .filter(a ->
                                a.getActivityStatus()
                                        == ActivityStatus.INQUEUE)
                        .filter(a ->
                                Objects.equals(
                                        a.getTime(),
                                        nextTime.get()))
                        .toList();

        activitiesToStart.forEach(a ->
                a.setActivityStatus(
                        ActivityStatus.IN_PROGRESS));

        session.setSessionStatus(
                SessionStatus.RUNNING);

        activityRepository.saveAll(
                activitiesToStart);

        sessionRepository.save(session);

        return ApiDataResponseBuilder.builder()
                .message(
                        "Berhasil menjalankan activity berikutnya"
                )
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build();

    }

    private boolean isTooEarly(LocalDateTime now, LocalDateTime targetTime) {
        return now.isBefore(targetTime.minusSeconds(TIME_TOLERANCE_SECONDS));
    }
}