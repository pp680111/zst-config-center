package com.zst.configcenter.server.module.version;

import com.zst.configcenter.server.module.config.ConfigUpdateEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class VersionUpdateNotifier implements ApplicationListener<ConfigUpdateEvent> {
    private static final int DEFAULT_POLLING_DURATION_MS = 30 * 1000;

    private final Map<String, List<CompletableFuture<Integer>>> pollingFutureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService pollingFutureCleaner = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void postConstruct() {
        pollingFutureCleaner.scheduleAtFixedRate(this::handleExpiredPollingFuture, 0,
                DEFAULT_POLLING_DURATION_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onApplicationEvent(ConfigUpdateEvent event) {
        if (!StringUtils.hasLength(event.getApp()) || !StringUtils.hasLength(event.getNamespace()) || !StringUtils.hasLength(event.getEnvironment())) {
            return;
        }

        String key = Version.buildVersionKey(event.getApp(), event.getNamespace(), event.getEnvironment());
        notifyPollingCounter(key, event.getVersion());
    }

    public CompletableFuture<Integer> requestVersionUpdateFuture(String key) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        pollingFutureMap.computeIfAbsent(key, k -> new ArrayList<>()).add(future);

        future.thenApply(version -> {
            List<CompletableFuture<Integer>> futures = pollingFutureMap.get(key);
            if (futures == null) {
                log.error("cancelling version update future for key {} failed, cannot find pollingQueue");
                return version;
            }

            futures.remove(future);
            return version;
        });
        return future;
    }

    /**
     * 唤醒等待者
     * @param key
     */
    private void notifyPollingCounter(String key, Integer version) {
        try {
            List<CompletableFuture<Integer>> pollingFutures = this.pollingFutureMap.get(key);
            if (pollingFutures != null) {
                pollingFutures.forEach(future -> future.complete(version));
            }
            pollingFutureMap.remove(key);
        } catch (Exception e) {
            log.error("notifyPollingCounter error", e);
        }
    }

    private void handleExpiredPollingFuture() {
        pollingFutureMap.forEach((key, futures) -> {
            futures.forEach(future -> {
                if (!future.isDone()) {
                    future.completeExceptionally(new TimeoutException());
                }
            });
        });
        pollingFutureMap.clear();
    }
}
