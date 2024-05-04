package com.zst.configcenter.server.module.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zst.configcenter.server.module.config.form.ConfigForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    @Autowired
    private ConfigMapper configMapper;

    public List<Config> list(String app, String namespace, String environment) {
        return configMapper.selectList(Wrappers.query(Config.class)
                .eq("app", app)
                .eq("namespace", namespace)
                .eq("environment", environment));
    }

    public void insertOrUpdate(ConfigForm form) {
        if (form == null || form.getConfigs() == null) {
            return;
        }

        form.getConfigs().forEach((k, v) -> {
            Config oldConfig = configMapper.selectOne(Wrappers.query(Config.class)
                    .eq("app", form.getApp())
                    .eq("namespace", form.getNamespace())
                    .eq("environment", form.getEnvironment())
                    .eq("config_key", k));

            if (oldConfig == null) {
                oldConfig = new Config();
                oldConfig.setApp(form.getApp());
                oldConfig.setNamespace(form.getNamespace());
                oldConfig.setEnvironment(form.getEnvironment());
                oldConfig.setConfigKey(k);
            }

            oldConfig.setConfigVal(v);

            if (oldConfig.getId() == null) {
                configMapper.insert(oldConfig);
            } else {
                configMapper.updateById(oldConfig);
            }
        });
    }

    public void deleteByKey(String app, String namespace, String environment, List<String> key) {
        configMapper.delete(Wrappers.query(Config.class)
                .eq("app", app)
                .eq("namespace", namespace)
                .eq("environment", environment)
                .in("config_key", key));
    }
}
