package com.zst.configcenter.server.module.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ConfigUpdateEvent extends ApplicationEvent {
    private String app;
    private String namespace;
    private String environment;
    private Integer version;

    public ConfigUpdateEvent(Object source, String app, String namespace, String environment, Integer version) {
        super(source);

        this.app = app;
        this.namespace = namespace;
        this.environment = environment;
        this.version = version;
    }
}
