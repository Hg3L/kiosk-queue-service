package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.core.model.entity.BaseSqlEntity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kiosk_logs", indexes = {
        @Index(name = "idx_syslog_created_at", columnList = "created_at")
})
@Getter
@Setter
public class LogEntity extends BaseSqlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private LogLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "component", nullable = false, length = 50)
    private LogComponent component;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "TEXT")
    private Map<String, Object> details = new HashMap<>();

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }

    public Map<String, Object> getDetails() {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        return this.details;
    }
}
