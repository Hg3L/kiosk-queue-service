package com.thh.kiosk.queue.modules.system.image;


import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;

@Getter
public enum ImageType {
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    ICO(".ico", "image/x-icon"),
    SVG(".svg", "image/svg+xml");

    private final String extension;
    private final String mimeType;

    ImageType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static Optional<ImageType> fromFilename(String filename) {
        if (filename == null || !filename.contains(".")) {
            return Optional.empty();
        }
        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return Arrays.stream(values())
                .filter(type -> type.getExtension().equals(ext))
                .findFirst();
    }
}
