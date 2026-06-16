package com.thh.kiosk.queue.modules.ticket;

import com.thh.kiosk.queue.core.model.entity.BaseSqlEntity;
import com.thh.kiosk.queue.modules.counter.CounterEntity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ticket")
@EqualsAndHashCode(
        onlyExplicitlyIncluded = true,
        callSuper = false
)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TicketEntity extends BaseSqlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @EqualsAndHashCode.Include
    @Column(
            name = "ticket_code",
            nullable = false,
            length = 5
    )
    String ticketCode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    TicketStatus status = TicketStatus.WAITING;

    @Column(name = "serving_at")
    private Instant servingAt;

    @Column(name = "completed_at")
    private Instant completedAt; // for skipped & completed

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "counter_id",
            nullable = false
    )
    CounterEntity counter;

    public void markAsServing() {
        this.status = TicketStatus.SERVING;
        this.servingAt = Instant.now();
    }

    public void markAsCompleted() {
        this.status = TicketStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void markAsSkipped() {
        this.status = TicketStatus.SKIPPED;
        this.completedAt = Instant.now();
    }
}
