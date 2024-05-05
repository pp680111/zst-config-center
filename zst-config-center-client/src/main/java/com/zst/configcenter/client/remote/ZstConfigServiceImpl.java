package com.zst.configcenter.client.remote;

import com.zst.configcenter.client.properties.ConfigServerProperties;
import com.zst.configcenter.client.remote.dto.ConfigDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ZstConfigServiceImpl implements ZstConfigService {
    private static final long DEFAULT_REFRESH_LOCK_WAIT_MS = 3_000L;

    private ReentrantLock configLock = new ReentrantLock();
    private Map<String, String> configs;

    private ConfigServerProperties configServerProperties;
    private RemoteServerClient remoteServerClient;

    public ZstConfigServiceImpl(ConfigServerProperties configServerProperties) {
        if (configServerProperties == null) {
            throw new RuntimeException("configServerProperties is null");
        }

        this.configServerProperties = configServerProperties;
        this.remoteServerClient = new RemoteServerClient(configServerProperties);
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

    private void refreshConfigs() {
        try {
            if (!configLock.tryLock(DEFAULT_REFRESH_LOCK_WAIT_MS, TimeUnit.MILLISECONDS)) {
                log.warn("config refresh lock wait timeout, skip");
            }

            List<ConfigDTO> configDTOs = remoteServerClient.list(configServerProperties.getApp(), configServerProperties.getNamespace(),
                    configServerProperties.getEnvironment());
            if (configDTOs == null) {
                return;
            }

            Map<String, String> newConfigMap = new HashMap<>();
            configDTOs.forEach(configDTO -> {
                newConfigMap.put(configDTO.getKey(), configDTO.getVal());
            });
            this.configs = newConfigMap;
        } catch (Exception e) {
            log.error("refresh config error", e);
        } finally {
            configLock.unlock();
        }
    }
}
