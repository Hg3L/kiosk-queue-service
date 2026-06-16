package com.thh.kiosk.queue.modules.counter;

import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.core.model.entity.BaseSqlEntity;
import com.thh.kiosk.queue.modules.ticket.TicketEntity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "counter")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CounterEntity extends BaseSqlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(
            name = "prefix",
            nullable = false,
            length = 1
    )
    String prefix;

    @Column(
            name = "current_sequence",
            nullable = false,
            length = 4
    )
    Integer currentSequence = 0;

    @Column(
            name = "name",
            nullable = false
    )
    String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(
            name = "ip",
            unique = true
    )
    String ip;

    @OneToMany(
            mappedBy = "counter",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    Set<TicketEntity> tickets = new HashSet<>();

    public void addTicket(TicketEntity ticket) {
        tickets.add(ticket);
        ticket.setCounter(this);
    }

    public void removeTicket(TicketEntity ticket) {
        tickets.remove(ticket);
        ticket.setCounter(null);
    }
}
