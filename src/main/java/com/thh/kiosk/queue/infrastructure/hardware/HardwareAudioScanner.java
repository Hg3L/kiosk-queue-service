package com.thh.kiosk.queue.infrastructure.hardware;

import org.springframework.stereotype.Component;

import javax.sound.sampled.*;

@Component
public class HardwareAudioScanner {

    public Mixer.Info getDefaultMixerInfo() {
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (mixer.isLineSupported(new Line.Info(SourceDataLine.class))) {
                return info;
            }
        }
        return null;
    }

    public String getAudioStatus() {

        Mixer.Info mixerInfo = getDefaultMixerInfo();

        if (mixerInfo == null) {
            return HardwareStatus.DISCONNECTED.name();
        }

        try {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);

            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class,
                    null
            );

            SourceDataLine line =
                    (SourceDataLine) mixer.getLine(info);

            line.open();
            line.close();

            return HardwareStatus.CONNECTED.name();

        } catch (Exception e) {
            return HardwareStatus.ERROR.name();
        }
    }
}
