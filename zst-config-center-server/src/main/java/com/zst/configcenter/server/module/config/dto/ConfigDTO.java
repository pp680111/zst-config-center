package com.zst.configcenter.server.module.config.dto;

import com.zst.configcenter.server.module.config.Config;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ConfigDTO {
    String id;
    String namespace;
    String environment;
    String key;
    String val;

    public static List<ConfigDTO> mapToDTOs(List<Config> configs) {
        if (configs == null) {
            return Collections.emptyList();
        }

        List<ConfigDTO> dtos = new ArrayList<>();
        for (Config config : configs) {
            dtos.add(mapToDTO(config));
        }

        return dtos;
    }

    public static ConfigDTO mapToDTO(Config config) {
        if (config == null) {
            return null;
        }

        ConfigDTO dto = new ConfigDTO();
        dto.setId(config.getId());
        dto.setNamespace(config.getNamespace());
        dto.setEnvironment(config.getEnvironment());
        dto.setKey(config.getConfigKey());
        dto.setVal(config.getConfigVal());
        return dto;
    }
}
