package com.thh.kiosk.queue.modules.counter;

import com.thh.kiosk.queue.core.model.enums.CommonStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

@Repository
public interface CounterRepository extends JpaRepository<CounterEntity, Long> {

    Optional<CounterEntity> findByIdAndStatus(Long id, CommonStatus status);

    Optional<CounterEntity> findByIdAndStatusAndIpIsNull(Long id, CommonStatus status);

    boolean existsByIdAndStatus(Long id, CommonStatus status);

    boolean existsByPrefixAndStatus(String prefix, CommonStatus status);

    List<CounterEntity> findAllByStatusOrderByCreatedAtAsc(CommonStatus status);

    List<CounterEntity> findAllByOrderByCreatedAtAsc();

    List<CounterEntity> findAllByStatus(CommonStatus status);

    int countAllByStatus(CommonStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CounterEntity c WHERE c.prefix = :prefix AND c.status = 'ACTIVE'")
    Optional<CounterEntity> findByPrefixWithLock(
            @Param("prefix") String prefix
    );
}
