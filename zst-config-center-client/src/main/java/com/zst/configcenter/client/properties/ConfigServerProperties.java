package com.zst.configcenter.client.properties;

import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Getter
public class ConfigServerProperties {
    private static final String PREFIX = "zst.config.server";
    private String address;
    private String app;
    private String namespace;
    private String environment;

    public static ConfigServerProperties fromEnvironment(Environment env) {
        String address = env.getProperty(getPropertyKey("address"));
        String app = env.getProperty(getPropertyKey("app"));
        String namespace = env.getProperty(getPropertyKey("namespace"));
        String environment = env.getProperty(getPropertyKey("environment"));

        if (!StringUtils.hasLength(address)) {
            throw new IllegalArgumentException("zst.config.server.address must be set");
        }

        if (!StringUtils.hasLength(app)) {
            throw new IllegalArgumentException("zst.config.server.app must be set");
        }

        ConfigServerProperties result = new ConfigServerProperties();
        result.address = address;
        result.app = app;
        result.namespace = Optional.ofNullable(namespace).orElse("default");
        result.environment = Optional.ofNullable(environment).orElse("default");
        return result;
    }

    private static String getPropertyKey(String name) {
        return String.format("%s.%s", PREFIX, name);
    }
}
