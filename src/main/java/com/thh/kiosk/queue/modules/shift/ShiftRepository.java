package com.thh.kiosk.queue.modules.shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<ShiftEntity, Long> {

    List<ShiftEntity> findAllByOrderByStartTimeAsc();

    @Query("SELECT COUNT(s) FROM ShiftEntity s WHERE " +
            "(:excludeId IS NULL OR s.id != :excludeId) AND " +
            "(s.startTime < :endTime AND s.endTime > :startTime)")
    long countOverlappingShifts(@Param("startTime") LocalTime startTime,
                                @Param("endTime") LocalTime endTime,
                                @Param("excludeId") Long excludeId);

    @Query("""
            SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
            FROM ShiftEntity s
            WHERE
            (
                :time BETWEEN s.startTime AND s.endTime
            )
            OR
            (
                    :time >= s.startTime
                    OR :time <= s.endTime
            )
    """)
    boolean existsShiftByWorkingTime(@Param("time") LocalTime time);
}
