package com.zst.configcenter.server.module.config.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Data
public class ConfigForm {
    @NotEmpty
    private String app;
    @NotEmpty
    private String namespace;
    @NotEmpty
    private String environment;
    private Map<String, String> configs;
}
