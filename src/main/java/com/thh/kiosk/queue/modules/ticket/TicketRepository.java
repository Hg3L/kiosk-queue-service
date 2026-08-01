package com.thh.kiosk.queue.modules.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    long countByCounterIdAndStatus(Long counterId, TicketStatus status);

    Optional<TicketEntity> findFirstByCounterIdAndStatus(Long counterId, TicketStatus status);

    Optional<TicketEntity> findFirstByCounterIdAndStatusOrderByCreatedAtAsc(Long counterId, TicketStatus status);

    @Query("SELECT MAX(CAST(SUBSTRING(t.ticketCode, LENGTH(:prefix) + 1) AS int)) " +
            "FROM TicketEntity t " +
            "WHERE t.ticketCode LIKE CONCAT(:prefix, '%') " +
            "AND t.createdAt >= :startOfShift AND t.createdAt < :endOfShift")
    Optional<Integer> findMaxSequenceByPrefixAndShift(
            @Param("prefix") String prefix,
            @Param("startOfShift") Instant startOfShift,
            @Param("endOfShift") Instant endOfShift
    );

    @Query("SELECT MAX(t.ticketCode) FROM TicketEntity t WHERE t.counter.prefix = :prefix AND t.createdAt >= :startOfDay")
    Optional<String> findMaxTicketCodeByPrefixAndDate(@Param("prefix") String prefix, @Param("startOfDay") Instant startOfDay);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TicketEntity t SET t.isDeleted = true, t.deletedAt = :now WHERE t.isDeleted = false AND t.createdAt < :startOfDay")
    int softDeleteAllTicketsBefore(@Param("startOfDay") Instant startOfDay, @Param("now") Instant now);

    @Query("SELECT t FROM TicketEntity t WHERE t.createdAt < :boundary ORDER BY t.createdAt ASC")
    List<TicketEntity> findTicketsForDailyReport(@Param("boundary") Instant boundary);

    @Query(value = "SELECT * FROM ticket WHERE created_at >= :startTime AND created_at <= :endTime", nativeQuery = true)
    List<TicketEntity> findAllTicketsForReport(
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
}
