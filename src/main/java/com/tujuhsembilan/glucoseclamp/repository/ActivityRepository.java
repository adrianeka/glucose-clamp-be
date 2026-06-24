package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.lang.Long;
import java.time.LocalDateTime;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    List<Activity> findAllActive();

    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    Page<Activity> findAllActive(Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.activityId = ?1 AND a.deletedAt IS NULL")
    Optional<Activity> findByIdAndDeletedAtIsNull(Long activityId);
    @Query("SELECT a FROM Activity a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    List<Activity> findBySessionIdAndDeletedAtIsNull(Long sessionId);

    @Query("SELECT a FROM Activity a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    Page<Activity> findBySessionIdAndDeletedAtIsNull(Long sessionId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL AND a.activityStatus <> com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus.COMPLETED")
    List<Activity> findBySessionIdAndDeletedAtIsNullAndNotCompleted(Long sessionId);
    @Query("SELECT a FROM Activity a WHERE a.actor.userId = ?1 AND a.deletedAt IS NULL")
    List<Activity> findByActorIdAndDeletedAtIsNull(Long actorId);

    Optional<Activity> findTopByDeletedAtIsNullOrderByActivityIdDesc();

    @Query("""
        SELECT COUNT(a) > 0
        FROM Activity a
        WHERE a.session.sessionId = :sessionId
        AND EXTRACT(HOUR FROM a.time) = :hour
        AND EXTRACT(MINUTE FROM a.time) = :minute
        AND a.deletedAt IS NULL
    """)
    boolean existsSameHourMinute(
            @Param("sessionId") Long sessionId,
            @Param("hour") int hour,
            @Param("minute") int minute
    );

    @Query("""
        SELECT COUNT(a) > 0
        FROM Activity a
        WHERE a.session.sessionId = :sessionId
        AND EXTRACT(HOUR FROM a.time) = :hour
        AND EXTRACT(MINUTE FROM a.time) = :minute
        AND a.activityId <> :activityId
        AND a.deletedAt IS NULL
    """)
    boolean existsSameHourMinuteExcludeId(
            @Param("sessionId") Long sessionId,
            @Param("hour") int hour,
            @Param("minute") int minute,
            @Param("activityId") Long activityId
    );

    Optional<Activity> findFirstBySessionSessionIdAndDeletedAtIsNull(Long sessionId);
}
