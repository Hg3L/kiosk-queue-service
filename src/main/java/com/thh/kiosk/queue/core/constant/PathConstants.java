package com.thh.kiosk.queue.core.constant;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathConstants {

    public static Path IMG_UPLOAD_DIR = Paths.get("C:/", "KioskQueue_Data", "uploads");
}
