package com.thh.kiosk.queue.modules.reset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface ResetTimeRepository extends JpaRepository<ResetTimeEntity, Long> {

    default LocalTime getCurrentResetTime() {
        return findById(1L)
                .map(ResetTimeEntity::getResetTime)
                .orElse(LocalTime.MIDNIGHT);
    }
}
