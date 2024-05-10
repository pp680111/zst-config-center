package com.zst.configcenter.server.module.version;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VersionService {
    private static final int DEFAULT_POLLING_DURATION_MS = 30 * 1000;

    private final Map<String, LinkedTransferQueue> pollingLockMap = new HashMap<>();
    private final Map<String, Integer> versionCacheMap = new ConcurrentHashMap<>();

    @Autowired
    private VersionMapper versionMapper;

    /**
     * 更新指定环境的配置版本号
     * @param app
     * @param namespace
     * @param environment
     */
    public void updateConfigVersion(String app, String namespace, String environment) {
        String versionId = buildVersionKey(app, namespace, environment);
        versionMapper.updateVersion(versionId);
        versionCacheMap.put(versionId, getConfigVersion(app, namespace, environment));
        notifyPollingCounter(buildVersionKey(app, namespace, environment));
    }

    /**
     * 获取当前指定配置的版本号
     * @param app
     * @param namespace
     * @param environment
     * @return
     */
    public Integer getConfigVersion(String app, String namespace, String environment) {
        Version result =  versionMapper.selectByKey(buildVersionKey(app, namespace, environment));
        return result == null ? 0 : result.getVersion();
    }

    /**
     * 获取指定配置的版本号，在未找到更高的版本号之前执行等待
     * @param app
     * @param namespace
     * @param environment
     * @param clientVersion
     * @return
     */
    public Integer getConfigVersion(String app, String namespace, String environment, Integer clientVersion) {
        Integer serverVersion = getConfigVersion(app, namespace, environment);
        if (clientVersion == null) {
            return serverVersion;
        }

        if (clientVersion < serverVersion) {
            return serverVersion;
        }

        try {
            // 如果正常等待并且被唤醒的话，就返回重新查询的版本号
            LinkedTransferQueue pollingQueue = getPollingQueue(buildVersionKey(app, namespace, environment));
            pollingQueue.tryTransfer(new Object(), DEFAULT_POLLING_DURATION_MS, TimeUnit.MILLISECONDS);
            Integer afterPollingWaitVersion = versionCacheMap.get(buildVersionKey(app, namespace, environment));
            return afterPollingWaitVersion == null ? serverVersion : afterPollingWaitVersion;
        } catch (Exception e) {
            log.error("getConfigVersion error", e);
        }
        // 默认情况返回之前查询的服务器版本号
        return serverVersion;
    }

    private String buildVersionKey(String app, String namespace, String environment) {
        return MessageFormat.format("{0}_{1}_{2}", app, namespace, environment);
    }

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

    private void notifyPollingCounter(String key) {
        try {
            LinkedTransferQueue pollingQueue = getPollingQueue(key);
            pollingQueue.clear();
        } catch (Exception e) {
            log.error("notifyPollingCounter error", e);
        }
    }
}
