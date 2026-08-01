package com.thh.kiosk.queue.modules.setting;

import com.thh.kiosk.queue.core.constant.WebSocketConstants;
import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.modules.system.image.ImageUploadService;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingService {

    private final SettingRepository settingRepository;

    private final ImageUploadService imageUploadService;

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<SettingKey, String> kioskCache = new ConcurrentHashMap<>();

    private String getOrLoad(SettingKey key) {
        return kioskCache.computeIfAbsent(key, k -> {
            log.debug("Cache miss for setting {}, loading from DB", k);

            return settingRepository.findByKey(k)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SETTING_NOT_FOUND))
                    .getValue();
        });
    }

    public List<SettingResponse> getSettingValues(SettingKey... keys) {
        return Arrays.stream(keys)
                .map(key -> SettingResponse.builder()
                        .key(key)
                        .value(getOrLoad(key))
                        .build())
                .toList();
    }

    public List<SettingResponse> getAllSettings() {
        return Arrays.stream(SettingKey.values())
                .map(key -> SettingResponse.builder()
                        .key(key)
                        .value(getOrLoad(key))
                        .build())
                .toList();
    }

    @Transactional
    public void updateSettings(List<SettingRequest> requests, MultipartFile file) {

        Map<SettingKey, String> requestMap = new HashMap<>();
        if (requests != null && !requests.isEmpty()) {
            requests.forEach(req -> requestMap.put(req.key(), req.value()));
        }

        if (file != null && !file.isEmpty()) {
            String newLogoUrl = imageUploadService.uploadLogo(file);
            requestMap.put(SettingKey.LOGO_URL, newLogoUrl);
        }

        if (requestMap.isEmpty()) {
            return;
        }

        List<SettingEntity> entities = settingRepository.findByKeyIn(requestMap.keySet());

        if (entities.size() != requestMap.size()) {
            throw new BusinessException(ErrorCode.SETTING_NOT_FOUND);
        }

        entities.forEach(entity -> entity.setValue(requestMap.get(entity.getKey())));
        settingRepository.saveAll(entities);

        entities.forEach(entity -> {
            kioskCache.remove(entity.getKey());
            log.info("{} Setting updated. Cache invalidated: {}", LogTag.SETTING, entity.getKey());
        });

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                messagingTemplate.convertAndSend(
                        WebSocketConstants.SETTING_DESTINATION,
                        WebSocketConstants.Payload.RELOAD_REQUIRED.name()
                );
            }
        });
    }
}
