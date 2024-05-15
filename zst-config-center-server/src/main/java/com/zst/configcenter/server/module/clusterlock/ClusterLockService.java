package com.zst.configcenter.server.module.clusterlock;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ClusterLockService {
    private static final String INSTANCE_ID = UUID.randomUUID().toString();
    /**
     * 默认的锁持有时间-30s
     */
    private static final int LOCK_TIMEOUT = 30;

    @Autowired
    private ClusterLockMapper clusterLockMapper;
    private ScheduledExecutorService lockUpdater;

    private boolean hasLock = false;

    @PostConstruct
    public void postConstruct() {
        lockUpdater = Executors.newSingleThreadScheduledExecutor();
        lockUpdater.scheduleAtFixedRate(this::tryLock, 0, 5, TimeUnit.SECONDS);
    }

    public boolean hasLock() {
        return hasLock;
    }

    private void tryLock() {
        try {
            String nextTimeout = LocalDateTime.now().plusSeconds(LOCK_TIMEOUT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int updatedRow = clusterLockMapper.tryOccupyLock(INSTANCE_ID, nextTimeout);
            hasLock = updatedRow > 0;
            log.info(MessageFormat.format("instance {0} get lock result = {1}", INSTANCE_ID, hasLock ? "true" : "false"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
