package com.zst.configcenter.client.remote;

import com.zst.configcenter.client.properties.ConfigServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

public class ZstConfigServiceFactory {
    public static ZstConfigService create(ApplicationContext applicationContext, Environment environment) {
        ConfigServerProperties configServerProperties = ConfigServerProperties.fromEnvironment(environment);
        return new ZstConfigServiceImpl(configServerProperties, applicationContext);
    }
}
