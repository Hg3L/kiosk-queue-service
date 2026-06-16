package com.thh.kiosk.queue.modules.setting;

import com.thh.kiosk.queue.core.model.entity.BaseSqlEntity;

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
@Table(name = "setting")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SettingEntity extends BaseSqlEntity {

    @Id
    @Column(name = "setting_key", length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private SettingKey key;

    @Column(name = "setting_value", length = 500)
    private String value;
}
