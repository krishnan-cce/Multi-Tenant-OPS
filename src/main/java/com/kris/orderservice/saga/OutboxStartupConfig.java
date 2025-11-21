package com.kris.orderservice.saga;

import com.kris.orderservice.outbox.domain.OutboxEvent;
import com.kris.orderservice.outbox.domain.OutboxStatus;
import com.kris.orderservice.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OutboxStartupConfig {

    private final OutboxEventRepository outboxEventRepository;

    @Bean
    public ApplicationRunner resetInProgressOnStartup() {
        return args -> resetInProgressEvents();
    }

    @Transactional
    public void resetInProgressEvents() {
        List<OutboxEvent> inProgressEvents =
                outboxEventRepository.findByStatus(OutboxStatus.IN_PROGRESS);

        if (inProgressEvents.isEmpty()) {
            log.info("No IN_PROGRESS outbox events to reset on startup.");
            return;
        }

        log.info("Resetting {} IN_PROGRESS events back to PENDING on startup",
                inProgressEvents.size());

        for (OutboxEvent event : inProgressEvents) {
            event.setStatus(OutboxStatus.PENDING);
        }
    }
}
