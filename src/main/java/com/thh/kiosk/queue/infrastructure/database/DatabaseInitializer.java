package com.thh.kiosk.queue.infrastructure.database;

import static com.thh.kiosk.queue.core.constant.PathConstants.IMG_UPLOAD_DIR;

import com.thh.kiosk.queue.config.properties.KioskDefault;
import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.modules.counter.CounterEntity;
import com.thh.kiosk.queue.modules.counter.CounterRepository;
import com.thh.kiosk.queue.modules.reset.ResetTimeEntity;
import com.thh.kiosk.queue.modules.reset.ResetTimeRepository;
import com.thh.kiosk.queue.modules.setting.SettingEntity;
import com.thh.kiosk.queue.modules.setting.SettingKey;
import com.thh.kiosk.queue.modules.setting.SettingRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final SettingRepository settingRepository;

    private final CounterRepository counterRepository;

    private final KioskDefault kioskDefault;

    private final ResetTimeRepository resetTimeRepository;

    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with default settings...");
        ensurePhysicalDefaultAssets();
        initDefaultSetting();
        mockCounterData();
        log.info("Database initialization completed.");
    }

    private void ensurePhysicalDefaultAssets() {
        try {
            if (Files.notExists(IMG_UPLOAD_DIR)) {
                Files.createDirectories(IMG_UPLOAD_DIR);
                log.info("Created upload directory at: {}", IMG_UPLOAD_DIR.toAbsolutePath());
            }

            String logoUrl = kioskDefault.logoUrl();
            String filename = logoUrl.substring(logoUrl.lastIndexOf("/") + 1);
            Path targetPath = IMG_UPLOAD_DIR.resolve(filename);

            if (Files.notExists(targetPath)) {
                Resource resource = resourceLoader.getResource("classpath:/static/uploads/" + filename);
                if (resource.exists()) {
                    try (InputStream is = resource.getInputStream()) {
                        Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        log.info("Successfully extracted default asset to physical drive: {}", targetPath.toAbsolutePath());
                    }
                } else {
                    log.warn("Default asset not found in classpath: {}", resource.getDescription());
                }
            }
        } catch (IOException e) {
            log.error("Critical error while ensuring physical default assets", e);
        }
    }

    private void initDefaultSetting() {

        Map<SettingKey, String> defaults = buildDefaultSettings();

        Set<SettingKey> existingKeys = settingRepository.findAll()
                .stream()
                .map(SettingEntity::getKey)
                .collect(Collectors.toSet());

        List<SettingEntity> missingSettings = defaults.entrySet()
                .stream()
                .filter(entry -> !existingKeys.contains(entry.getKey()))
                .map(entry -> {
                    log.info(
                            "Default setting for {} not found. Creating with value: {}",
                            entry.getKey(),
                            entry.getValue()
                    );

                    SettingEntity setting = new SettingEntity();
                    setting.setKey(entry.getKey());
                    setting.setValue(entry.getValue());
                    return setting;
                })
                .toList();

        settingRepository.saveAll(missingSettings);
    }

    private Map<SettingKey, String> buildDefaultSettings() {
        return Map.of(
                SettingKey.KIOSK_TITLE, kioskDefault.name(),
                SettingKey.LOGO_URL, kioskDefault.logoUrl(),

                SettingKey.CUSTOMER_COLOR_PRIMARY, kioskDefault.customerColorPrimary(),
                SettingKey.CUSTOMER_COLOR_SECONDARY, kioskDefault.customerColorSecondary(),
                SettingKey.CUSTOMER_COLOR_BACKGROUND, kioskDefault.customerColorBackground(),
                SettingKey.CUSTOMER_COLOR_TEXT_HIGHLIGHT, kioskDefault.customerColorTextHighlight(),

                SettingKey.VIEWER_COLOR_PRIMARY, kioskDefault.viewerColorPrimary(),
                SettingKey.VIEWER_COLOR_SECONDARY, kioskDefault.viewerColorSecondary(),
                SettingKey.VIEWER_COLOR_BACKGROUND, kioskDefault.viewerColorBackground(),
                SettingKey.VIEWER_COLOR_TEXT_HIGHLIGHT, kioskDefault.viewerColorTextHighlight()
        );
    }

    private void mockCounterData() {
        if(counterRepository.count() > 0) {
            log.info("Counter data already exists. Skipping mock data insertion.");
            return;
        }
        List<CounterEntity> mockDataList = List.of(
                new CounterEntity(null, "A", 0, "CHỨNG THỰC BẢN SAO - CHỮ KÝ", CommonStatus.ACTIVE, null, null),
                new CounterEntity(null, "B", 0, "THU PHÍ - LỆ PHÍ", CommonStatus.ACTIVE, null, null),
                new CounterEntity(null, "C", 0, "BẢO TRỢ XÃ HỘI - CHÍNH SÁCH NGƯỜI CÓ CÔNG - GIÁO DỤC VĂN HÓA", CommonStatus.ACTIVE, null, null),
                new CounterEntity(null, "D", 0, "KINH DOANH - HỢP TÁC XÃ - CÔNG THƯƠNG - GIẢM NGHÈO", CommonStatus.ACTIVE, null, null),
                new CounterEntity(null, "E", 0, "ĐẤT ĐAI - MÔI TRƯỜNG - XÂY DỰNG - CUNG CẤP THÔNG TIN QUY HOẠCH", CommonStatus.ACTIVE, null, null),
                new CounterEntity(null, "F", 0, "ĐĂNG KÝ KHAI TỬ - XÁC NHẬN TÌNH TRẠNG HÔN NHÂN", CommonStatus.ACTIVE,null, null),
                new CounterEntity(null, "G", 0, "ĐĂNG KÝ KẾT HÔN - CẢI CHÍNH HỘ TỊCH", CommonStatus.ACTIVE, null, null),
                new CounterEntity(null, "H", 0, "ĐĂNG KÝ KHAI SINH - TRÍCH LỤC BẢN SAO HỘ TỊCH", CommonStatus.ACTIVE, null, null)
        );
        counterRepository.saveAll(mockDataList);
        log.info("Inserted {} mock counter records into the database.", mockDataList.size());
    }

    private void mockResetTime() {
        if(resetTimeRepository.count() > 0) {
            log.info("Reset time already exists. Skipping mock data insertion.");
            return;
        }
        resetTimeRepository.save(new ResetTimeEntity());
         log.info("Inserted default reset time record into the database.");
    }
}
