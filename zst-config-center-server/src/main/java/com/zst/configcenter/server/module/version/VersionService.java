package com.zst.configcenter.server.module.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class VersionService {
    private static final int DEFAULT_POLLING_DURATION_MS = 30 * 1000;

    private final Map<String, Semaphore> pollingSemaphoreMap = new ConcurrentHashMap<>();
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
    }

    public Integer getConfigVersion(String app, String namespace, String environment) {
        Version result =  versionMapper.selectByKey(buildVersionKey(app, namespace, environment));
        return result == null ? 0 : result.getVersion();
    }

    private String buildVersionKey(String app, String namespace, String environment) {
        return MessageFormat.format("{0}_{1}_{2}", app, namespace, environment);
    }

    private Semaphore getPollingSemaphore(String key) {
        if (!pollingSemaphoreMap.containsKey(key)) {
            synchronized (this) {
                if (!pollingSemaphoreMap.containsKey(key)) {
                    pollingSemaphoreMap.put(key, new Semaphore());
                }
            }
        }
    }
}
