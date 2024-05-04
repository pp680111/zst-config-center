package com.zst.configcenter.server.module.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
}
