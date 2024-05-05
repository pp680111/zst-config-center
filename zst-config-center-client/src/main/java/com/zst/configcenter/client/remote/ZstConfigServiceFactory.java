package com.zst.configcenter.client.remote;

import com.zst.configcenter.client.properties.ConfigServerProperties;
import org.springframework.core.env.Environment;

public class ZstConfigServiceFactory {
    public static ZstConfigService create(Environment environment) {
        ConfigServerProperties configServerProperties = ConfigServerProperties.fromEnvironment(environment);
        return new ZstConfigServiceImpl(configServerProperties);
    }
}
