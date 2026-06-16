package com.thh.kiosk.queue.modules.system.image;

import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.modules.system.log.LogTag;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageUploadService {

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
    private static final String PREFIX_NAME = "custom-logo-";
    private static final long MAX_RAW_SIZE = 2 * 1024 * 1024;
    private static final int MAX_WIDTH = 512;
    private static final int MAX_HEIGHT = 512;

    public String uploadLogo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        if (file.getSize() > MAX_RAW_SIZE) {
            log.warn("{} File upload vượt quá 5MB: {} bytes", LogTag.IMAGE, file.getSize());
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
        }

        String originalFilename = file.getOriginalFilename();
        ImageType imageType = ImageType.fromFilename(originalFilename)
                .orElseThrow(() -> {
                    log.warn("{} File format unsupported: {}", LogTag.IMAGE, originalFilename);
                    return new BusinessException(ErrorCode.FILE_FORMAT_UNSUPPORTED);
                });

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase(imageType.getMimeType())) {
            log.warn("{} File format is not valid! Extension: {}, Real MimeType: {}",
                    LogTag.IMAGE,
                    imageType.getExtension(),
                    contentType
            );
            throw new BusinessException(ErrorCode.SETTING_INVALID);
        }

        try {
            if (Files.notExists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("{} Folder created at: {}", LogTag.IMAGE, uploadDir.toAbsolutePath());
            }

            cleanUpOldLogos();

            String extension = imageType.getExtension();
            String uuidPart = UUID.randomUUID().toString().substring(0, 8);
            String newFilename;
            Path destination;

            if (extension.equals(".svg") || extension.equals(".ico")) {
                newFilename = PREFIX_NAME + uuidPart + extension;
                destination = uploadDir.resolve(newFilename);
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                log.info("{} Saved RAW logo at: {}", LogTag.IMAGE, destination.toAbsolutePath());
            } else {
                newFilename = PREFIX_NAME + uuidPart + ".png";
                destination = uploadDir.resolve(newFilename);

                Thumbnails.of(file.getInputStream())
                        .size(MAX_WIDTH, MAX_HEIGHT)
                        .outputFormat("png")
                        .outputQuality(0.9)
                        .toFile(destination.toFile());

                log.info("{} Resized & Compressed logo saved at: {}", LogTag.IMAGE, destination.toAbsolutePath());
            }

            return "/uploads/" + newFilename;

        } catch (IOException e) {
            log.error("{} Error I/O when upload logo", LogTag.IMAGE, e);
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    private void cleanUpOldLogos() {
        try (Stream<Path> files = Files.list(uploadDir)) {
            files.filter(p -> p.getFileName().toString().startsWith(PREFIX_NAME))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            log.warn("{} Can't delete old logo: {}", LogTag.IMAGE, p.getFileName(), e);
                        }
                    });
        } catch (IOException e) {
            log.warn("{} Failed to read upload directory for cleanup", LogTag.IMAGE, e);
        }
    }
}