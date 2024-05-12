package com.zst.configcenter.server.module.version;

import com.zst.configcenter.server.module.config.ConfigUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VersionUpdateNotifier implements ApplicationListener<ConfigUpdateEvent> {
    private static final int DEFAULT_POLLING_DURATION_MS = 30 * 1000;

    private final Map<String, LinkedTransferQueue> pollingLockMap = new HashMap<>();

    @Override
    public void onApplicationEvent(ConfigUpdateEvent event) {
        if (!StringUtils.hasLength(event.getApp()) || !StringUtils.hasLength(event.getNamespace()) || !StringUtils.hasLength(event.getEnvironment())) {
            return;
        }

        String key = Version.buildVersionKey(event.getApp(), event.getNamespace(), event.getEnvironment());
        notifyPollingCounter(key);
    }

    public void waitForUpdate(String key) {
        try {
            LinkedTransferQueue pollingQueue = getPollingQueue(key);
            pollingQueue.tryTransfer(new Object(), DEFAULT_POLLING_DURATION_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("error when waiting for version update");
        }
    }

    /**
     * 获取用于执行等待的阻塞队列对象
     * @param key
     * @return
     */
    private LinkedTransferQueue getPollingQueue(String key) {
        if (!pollingLockMap.containsKey(key)) {
            synchronized (this) {
                if (!pollingLockMap.containsKey(key)) {
                    pollingLockMap.put(key, new LinkedTransferQueue());
                }
            }
        }

        return pollingLockMap.get(key);
    }

    /**
     * 唤醒等待者
     * @param key
     */
    private void notifyPollingCounter(String key) {
        try {
            // 通过清空阻塞队列的元素的方式，来唤醒所有等待阻塞队列的线程
            LinkedTransferQueue pollingQueue = getPollingQueue(key);
            pollingQueue.clear();
        } catch (Exception e) {
            log.error("notifyPollingCounter error", e);
        }
    }

}
