package com.thh.kiosk.queue.config.audit;

import com.thh.kiosk.queue.config.security.UserContextHolder;
import com.thh.kiosk.queue.core.constant.RoleSessionConstants;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        String currentUser = UserContextHolder.getCurrentUser();

        return Optional.of(
                Objects.requireNonNullElse(
                        currentUser,
                        RoleSessionConstants.SYSTEM
                )
        );
    }
}
