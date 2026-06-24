package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionActivityItemResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionTimelineResponse;
import com.tujuhsembilan.glucoseclamp.exception.classes.DataNotFoundException;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus;
import com.tujuhsembilan.glucoseclamp.model.base.SessionStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
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

@Service
@RequiredArgsConstructor
public class SessionTrackingService {

    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;
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

        return ApiDataResponseBuilder.builder()
                .data(response)
                .message("Berhasil mendapatkan timeline session")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private SessionActivityItemResponse toActivityItemResponse(Activity activity) {
        return new SessionActivityItemResponse(
                activity.getActivityId(),
                activity.getTime(),
                activity.getActivityType(),
                activity.getActivityDesc(),
                activity.getPhaseCode(),
                activity.getPhaseName(),
                activity.getActivityStatus(),
                activity.getMinute()
        );
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

        // Ambil semua activity yang sedang IN_PROGRESS
        List<Activity> currentActivities = activities.stream()
                .filter(a -> a.getActivityStatus() == ActivityStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        /*
        * ============================================================
        * BELUM ADA ACTIVITY YANG BERJALAN
        * ============================================================
        */
        if (currentActivities.isEmpty()) {

            // Jika semua activity sudah selesai
            if (activities.stream().allMatch(this::isCompletedActivity)) {
                return List.of();
            }

            // Session belum dimulai -> tampilkan activity pertama
            Optional<LocalDateTime> firstTime = activities.stream()
                    .map(Activity::getTime)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .findFirst();

            if (firstTime.isEmpty()) {
                return List.of();
            }

            return activities.stream()
                    .filter(a -> a.getActivityStatus() != ActivityStatus.COMPLETED)
                    .filter(a -> Objects.equals(a.getTime(), firstTime.get()))
                    .collect(Collectors.toList());
        }

        /*
        * ============================================================
        * CARI WAKTU ACTIVITY BERIKUTNYA
        * ============================================================
        */

        // Semua IN_PROGRESS pasti memiliki waktu yang sama,
        // ambil salah satunya.
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

        // Ambil semua activity pada waktu berikutnya
        return activities.stream()
                .filter(a -> Objects.equals(a.getTime(), nextTime.get()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ApiDataResponseBuilder nextProgressActivity(Long sessionId) {

        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId)
                .orElseThrow(() -> new DataNotFoundException("Data session tidak ditemukan"));

        List<Activity> activities = activityRepository.findBySessionIdAndDeletedAtIsNull(sessionId)
                .stream()
                .sorted(
                        Comparator.comparing(Activity::getTime, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(Activity::getActivityId))
                .collect(Collectors.toList());

        if (activities.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Activity tidak ditemukan")
                    .status(HttpStatus.NOT_FOUND)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();

        /*
        * ============================================================
        * CARI ACTIVITY YANG SEDANG BERJALAN
        * ============================================================
        */

        List<Activity> currentActivities = activities.stream()
                .filter(a -> a.getActivityStatus() == ActivityStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        /*
        * ============================================================
        * SESSION BELUM DIMULAI
        * ============================================================
        */

        if (currentActivities.isEmpty()) {

            Optional<LocalDateTime> firstTime = activities.stream()
                    .map(Activity::getTime)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .findFirst();

            if (firstTime.isEmpty()) {
                return ApiDataResponseBuilder.builder()
                        .message("Activity tidak memiliki waktu")
                        .status(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build();
            }

            if (isTooEarly(now, firstTime.get())) {
                return ApiDataResponseBuilder.builder()
                        .message("Belum waktunya memulai session")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build();
            }

            // Activity pertama menjadi IN_PROGRESS
            activities.stream()
                    .filter(a -> Objects.equals(a.getTime(), firstTime.get()))
                    .forEach(a -> a.setActivityStatus(ActivityStatus.IN_PROGRESS));

            session.setSessionStatus(SessionStatus.RUNNING);

            activityRepository.saveAll(activities);
            sessionRepository.save(session);

            return ApiDataResponseBuilder.builder()
                    .message("Activity pertama berhasil dijalankan")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();
        }

        /*
        * ============================================================
        * CARI WAKTU ACTIVITY BERIKUTNYA
        * ============================================================
        */

        LocalDateTime currentTime = currentActivities.get(0).getTime();

        Optional<LocalDateTime> nextTime = activities.stream()
                .map(Activity::getTime)
                .filter(Objects::nonNull)
                .filter(time -> time.isAfter(currentTime))
                .distinct()
                .sorted()
                .findFirst();

        /*
        * ============================================================
        * TIDAK ADA NEXT
        * ============================================================
        */

        if (nextTime.isEmpty()) {

            // Activity terakhir baru boleh COMPLETE jika sudah waktunya
            if (isTooEarly(now, currentTime)) {
                return ApiDataResponseBuilder.builder()
                        .message("Activity terakhir masih berjalan")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build();
            }

            activities.stream()
                    .filter(a -> Objects.equals(a.getTime(), currentTime))
                    .forEach(a -> a.setActivityStatus(ActivityStatus.COMPLETED));

            session.setSessionStatus(SessionStatus.COMPLETED);

            activityRepository.saveAll(activities);
            sessionRepository.save(session);

            return ApiDataResponseBuilder.builder()
                    .message("Session selesai")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();
        }

        /*
        * ============================================================
        * BELUM WAKTUNYA PINDAH
        * ============================================================
        */
       System.out.println("Date Now ::"+ now);
       System.out.println("Toleransi semenit ::"+ isTooEarly(now, nextTime.get()));
        if (isTooEarly(now, nextTime.get())) {
            return ApiDataResponseBuilder.builder()
                    .message("Activity saat ini masih berjalan")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build();
        }

        /*
        * ============================================================
        * COMPLETE CURRENT
        * ============================================================
        */

        activities.stream()
                .filter(a -> Objects.equals(a.getTime(), currentTime))
                .forEach(a -> a.setActivityStatus(ActivityStatus.COMPLETED));

        /*
        * ============================================================
        * NEXT MENJADI IN_PROGRESS
        * ============================================================
        */

        activities.stream()
                .filter(a -> Objects.equals(a.getTime(), nextTime.get()))
                .forEach(a -> a.setActivityStatus(ActivityStatus.IN_PROGRESS));

        session.setSessionStatus(SessionStatus.RUNNING);

        activityRepository.saveAll(activities);
        sessionRepository.save(session);

        return ApiDataResponseBuilder.builder()
                .message("Berhasil berpindah ke activity berikutnya")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    private boolean isTooEarly(LocalDateTime now, LocalDateTime targetTime) {
        return now.isBefore(targetTime.minusSeconds(TIME_TOLERANCE_SECONDS));
    }
}