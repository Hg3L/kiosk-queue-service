package com.thh.kiosk.queue.modules.reset;

import java.time.LocalTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reset_time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetTimeEntity {

    @Id
    private Long id = 1L;

    @Column(name = "reset_time", nullable = false)
    private LocalTime resetTime = LocalTime.of(0, 0);

    @Column(name = "export_path", length = 500)
    private String exportPath = "C:/Kiosk_Reports";

    @Column(name = "last_edit_month", length = 7)
    private String lastEditMonth;

    @Column(name = "edit_count")
    private int editCount = 0;
}
