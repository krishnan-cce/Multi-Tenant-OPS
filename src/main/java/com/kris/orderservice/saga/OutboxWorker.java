package com.kris.orderservice.saga;

import com.kris.orderservice.outbox.domain.OutboxEvent;
import com.kris.orderservice.outbox.domain.OutboxStatus;
import com.kris.orderservice.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxWorker {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxProcessor outboxProcessor;

    @Scheduled(fixedDelay = 5000)
    public void runWorker() {

        List<OutboxEvent> pendingEvents =
                outboxEventRepository.findTop100ByStatusOrderByIdAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("OutboxWorker: processing {} PENDING events", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            Long eventId = event.getId();

            boolean claimed = outboxProcessor.markInProgress(eventId);
            if (!claimed) {
                continue;
            }

            try {
                outboxProcessor.processEvent(eventId);
            } catch (Exception ex) {
                log.error("Error while processing outbox event id={}", eventId, ex);
            }
        }
    }
}
