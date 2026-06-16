package com.thh.kiosk.queue.modules.audio.strategy;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AudioStrategyResolver {

    private final Map<AudioPlaybackMode, AudioPlaybackStrategy> strategyMap;

    public AudioStrategyResolver(List<AudioPlaybackStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                                AudioPlaybackStrategy::getSupportedMode,
                                Function.identity()
                        )
                );
    }

    public AudioPlaybackStrategy resolve(AudioPlaybackMode mode) {
        AudioPlaybackStrategy strategy = strategyMap.get(mode);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported audio playback mode: " + mode);
        }
        return strategy;
    }
}
