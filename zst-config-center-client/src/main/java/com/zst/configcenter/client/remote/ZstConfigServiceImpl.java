package com.zst.configcenter.client.remote;

import com.alibaba.fastjson2.JSON;
import com.zst.configcenter.client.properties.ConfigServerProperties;
import com.zst.configcenter.client.remote.dto.ConfigDTO;
import com.zst.configcenter.client.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ZstConfigServiceImpl implements ZstConfigService {
    private static final long DEFAULT_REFRESH_LOCK_WAIT_MS = 3_000L;
    private static final int DEFAULT_REFRESH_INTERVAL_MS = 5_000;
    private static final int DEFAULT_INIT_VERSION_REFRESH_DELAY_MS = 10_000;

    private ScheduledExecutorService scheduler;
    private ReentrantLock configLock = new ReentrantLock();
    private Map<String, String> configs;
    private Integer version;

    private ConfigServerProperties configServerProperties;
    private RemoteServerClient remoteServerClient;
    private ApplicationContext applicationContext;

    public ZstConfigServiceImpl(ConfigServerProperties configServerProperties, ApplicationContext applicationContext) {
        if (configServerProperties == null) {
            throw new RuntimeException("configServerProperties is null");
        }
        if (applicationContext == null) {
            throw new RuntimeException("applicationContext is null");
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();

        this.configServerProperties = configServerProperties;
        this.remoteServerClient = new RemoteServerClient(configServerProperties);
        this.applicationContext = applicationContext;

        init();
    }

    @Override
    public String[] getPropertyNames() {
        if (configs == null) {
            refreshConfigs();
        }

        if (configs == null) {
            return new String[0];
        }

        return this.configs.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        if (configs == null) {
            refreshConfigs();
        }

        if (configs == null) {
            return null;
        }

        return configs.get(name);
    }

    public void init() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                scheduledConfigVersionRefresher();
            } catch (Exception e) {
                log.error("scheduled version refresher execution error", e);
            }
        }, DEFAULT_INIT_VERSION_REFRESH_DELAY_MS, DEFAULT_REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    private void refreshConfigs() {
        try {
            if (!configLock.tryLock(DEFAULT_REFRESH_LOCK_WAIT_MS, TimeUnit.MILLISECONDS)) {
                log.warn("config refresh lock wait timeout, skip");
                return;
            }

            List<ConfigDTO> configDTOs = remoteServerClient.list(configServerProperties.getApp(), configServerProperties.getNamespace(),
                    configServerProperties.getEnvironment());
            if (configDTOs == null) {
                return;
            }

            int newVersion = remoteServerClient.version(configServerProperties.getApp(), configServerProperties.getNamespace(),
                    configServerProperties.getEnvironment(), this.version);

            Map<String, String> newConfigMap = new HashMap<>();
            configDTOs.forEach(configDTO -> {
                newConfigMap.put(configDTO.getKey(), configDTO.getVal());
            });

            log.debug("refreshed configs = " + JSON.toJSONString(newConfigMap));
            log.debug("refreshed version = " + newVersion);

            Set<String> changedKeys = calculateChangedKey(this.configs, newConfigMap);
            this.configs = newConfigMap;
            this.version = newVersion;

            if (!changedKeys.isEmpty()) {
                log.debug("publish environmentChangeEvent, changeKeys = " + JSON.toJSONString(changedKeys));
                this.applicationContext.publishEvent(new EnvironmentChangeEvent(changedKeys));
            }

            log.info(MessageFormat.format("refresh config from remote server done, current version = {0}", version));
        } catch (Exception e) {
            log.error("refresh config error", e);
        } finally {
            configLock.unlock();
        }
    }

    private void scheduledConfigVersionRefresher() {
        log.debug("checking remote server config version");
        int newVersion = remoteServerClient.version(configServerProperties.getApp(), configServerProperties.getNamespace(),
                configServerProperties.getEnvironment(), this.version);

        log.debug(MessageFormat.format("currentVersion = {0}, newVersion = {1}", this.version, newVersion));
        if (newVersion > this.version) {
            refreshConfigs();
        }
    }

    private Set<String> calculateChangedKey(Map<String, String> oldConfigMap, Map<String, String> newConfigMap) {
        if (oldConfigMap == null) {
            return newConfigMap.keySet();
        }
        if (newConfigMap == null) {
            return oldConfigMap.keySet();
        }

        // 变化的key包括old比new多的key（被删除了），new比old多的key（新增了），old和new都有的key，但是值不同（修改了）
        Set<String> result = new HashSet<>();
        newConfigMap.forEach((key, value) -> {
            if (!oldConfigMap.containsKey(key)) {
                result.add(key);
            } else if (!StringUtils.equals(oldConfigMap.get(key), newConfigMap.get(key))) {
                result.add(key);
            }
        });
        oldConfigMap.keySet().forEach(key -> {
            if (!newConfigMap.containsKey(key)) {
                result.add(key);
            }
        });

        return result;
    }
}
