package com.thh.kiosk.queue.modules.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SettingRepository extends JpaRepository<SettingEntity, SettingKey> {

    Optional<SettingEntity> findByKey(SettingKey key);

    List<SettingEntity> findByKeyIn(Set<SettingKey> keys);

    boolean existsByKey(SettingKey key);
}
