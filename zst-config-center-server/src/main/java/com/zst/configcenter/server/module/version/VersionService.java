package com.zst.configcenter.server.module.version;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class VersionService {
    @Autowired
    private VersionMapper versionMapper;

    /**
     * 更新指定环境的配置版本号
     * @param app
     * @param namespace
     * @param environment
     */
    public void updateConfigVersion(String app, String namespace, String environment) {
        String versionId = buildVersionKey(app, namespace, environment);
        versionMapper.updateVersion(versionId);
    }

    public Integer getConfigVersion(String app, String namespace, String environment) {
        Version result =  versionMapper.selectByKey(buildVersionKey(app, namespace, environment));
        return result == null ? 0 : result.getVersion();
    }

    private String buildVersionKey(String app, String namespace, String environment) {
        return MessageFormat.format("{0}_{1}_{2}", app, namespace, environment);
    }
}
