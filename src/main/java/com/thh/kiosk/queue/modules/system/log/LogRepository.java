package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.core.repository.CustomSliceRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends
        JpaRepository<LogEntity, Long>,
        CustomSliceRepository<LogEntity> {

}
