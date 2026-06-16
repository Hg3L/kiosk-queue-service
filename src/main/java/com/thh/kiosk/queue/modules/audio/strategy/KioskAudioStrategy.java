package com.thh.kiosk.queue.modules.audio.strategy;

import com.thh.kiosk.queue.infrastructure.hardware.HardwareAudioScanner;
import com.thh.kiosk.queue.modules.system.log.AbstractLogWriter;
import com.thh.kiosk.queue.modules.system.log.LogActionEnum;
import com.thh.kiosk.queue.modules.system.log.LogTag;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KioskAudioStrategy
        extends AbstractLogWriter
        implements AudioPlaybackStrategy {
    private final HardwareAudioScanner audioScanner;

    private final Map<String, byte[]> audioRamCache = new ConcurrentHashMap<>();

    private static final int BUFFER_SIZE = 4096;

    @Override
    public AudioPlaybackMode getSupportedMode() {
        return AudioPlaybackMode.KIOSK;
    }

    /*@PostConstruct
    public void preloadAudioToRam() {
        log.info("{} Load audio file to RAM Kiosk", LogTag.AUDIO_FALLBACK);
        List<String> filesToCache = new ArrayList<>();
        filesToCache.add("audio/start.wav");
        for (int i = 0; i <= 9; i++) {
            filesToCache.add("audio/number/" + i + ".wav");
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            filesToCache.add("audio/alphabet/" + c + ".wav");
        }

        for (String path : filesToCache) {
            try (InputStream is = new ClassPathResource(path).getInputStream()) {
                audioRamCache.put(path, is.readAllBytes());
            } catch (Exception e) {
                log.trace("{} {} not found, skip load to RAM", LogTag.AUDIO_FALLBACK, path);
            }
        }
    }*/

    @Override
    public void play(String ticketCode, List<String> playlist) {
        log.info("{} Fallback to play audio in Kiosk", LogTag.AUDIO_FALLBACK);
        SourceDataLine line = null;
        try {
            Mixer hardwareMixer = getHardwareMixer();
            byte[] buffer = new byte[BUFFER_SIZE];

            for (String relativePath : playlist) {
                try (AudioInputStream ais = getAudioStream(relativePath)) {
                    if (ais == null) continue;

                    if (line == null) {
                        AudioFormat format = ais.getFormat();
                        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

                        if (hardwareMixer != null && hardwareMixer.isLineSupported(info)) {
                            line = (SourceDataLine) hardwareMixer.getLine(info);
                        } else {
                            line = (SourceDataLine) AudioSystem.getLine(info);
                        }

                        line.open(format);
                        line.start();
                    }

                    int bytesRead;
                    while ((bytesRead = ais.read(buffer, 0, buffer.length)) != -1) {
                        line.write(buffer, 0, bytesRead);
                    }
                }
            }
            log.info("{} Audio call ticket {} in Kiosk has been played",
                    LogTag.AUDIO_FALLBACK,
                    ticketCode);
            logInfo(
                    LogActionEnum.KIOSK_AUDIO_PLAY.buildParam(
                            null,
                            Map.of(
                                    "ticket_code", ticketCode,
                                    "playlist", playlist
                            )
                    )
            );
        } catch (Exception e) {
            log.error("{} Can't connect to audio device", LogTag.AUDIO_FALLBACK, e);
            logError(LogActionEnum.AUDIO_DEVICE_NOT_FOUND.buildParam(
                    null,
                    Map.of(
                            "error_message", e.getMessage()
                    )
            ));
        } finally {
            if (line != null) {
                line.drain();
                line.close();
            }
        }
    }

    private Mixer getHardwareMixer() {
        Mixer.Info info = audioScanner.getDefaultMixerInfo();
        if (info != null) {
            try { return AudioSystem.getMixer(info); }
            catch (Exception ignored) {}
        }
        return null;
    }

    private AudioInputStream getAudioStream(String relativePath) {
        try {
            InputStream rawStream = new ClassPathResource(relativePath).getInputStream();
            InputStream bufferedStream = new BufferedInputStream(rawStream, 1024 * 1024);

            return AudioSystem.getAudioInputStream(bufferedStream);
        } catch (Exception e) {
            log.warn("Can't find audio file: {}", relativePath);
            logError(LogActionEnum.AUDIO_FILE_NOT_FOUND.buildParam(
                    null,
                    Map.of(
                            "file_path", relativePath,
                            "error_message", e.getMessage()
                    )
            ));
            return null;
        }
    }

}
