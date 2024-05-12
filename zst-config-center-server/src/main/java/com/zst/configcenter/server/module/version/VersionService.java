package com.zst.configcenter.server.module.version;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Slf4j
@Service
public class VersionService {
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private VersionUpdateNotifier versionUpdateNotifier;

    /**
     * 更新指定环境的配置版本号
     * @param app
     * @param namespace
     * @param environment
     */
    public void updateConfigVersion(String app, String namespace, String environment) {
        String versionId = Version.buildVersionKey(app, namespace, environment);
        versionMapper.updateVersion(versionId);
        /*
            当事务更新配置的事务尚未提交时，唤醒了等待中的长轮询，会导致客户端立刻发起查询配置文件的请求，而此时事务在数据库中又还没提交，
            导致即使查询到版本号更新了，再次发起的配置查询请求读取到的还是就数据
         */
    }

    /**
     * 获取当前指定配置的版本号
     * @param app
     * @param namespace
     * @param environment
     * @return
     */
    public Integer getConfigVersion(String app, String namespace, String environment) {
        Version result =  versionMapper.selectByKey(Version.buildVersionKey(app, namespace, environment));
        return result == null ? 0 : result.getVersion();
    }

    /**
     * 获取指定配置的版本号，在未找到更高的版本号之前执行等待
     * @param app
     * @param namespace
     * @param environment
     * @param clientVersion
     * @return
     */
    public Integer getConfigVersion(String app, String namespace, String environment, Integer clientVersion) {
        Integer serverVersion = queryVersion(app, namespace, environment);
        log.debug(MessageFormat.format("handling client request,clientVersion={0} serverVersion: {1}", clientVersion, serverVersion));

        if (clientVersion == null) {
            return serverVersion;
        }

        if (clientVersion < serverVersion) {
            return serverVersion;
        }

        try {
            // 如果正常等待并且被唤醒的话，就返回重新查询的版本号
            versionUpdateNotifier.waitForUpdate(Version.buildVersionKey(app, namespace, environment));
            return queryVersion(app, namespace, environment);
        } catch (Exception e) {
            log.error("getConfigVersion error", e);
        }
        // 默认情况返回之前查询的服务器版本号
        return serverVersion;
    }

    /**
     * 查询当前指定配置key的版本
     *
     * 先从缓存中查询，如果缓存中尚不存在的话，再从数据库中查询
     * @param app
     * @param namespace
     * @param environment
     * @return
     */
    private Integer queryVersion(String app, String namespace, String environment) {
        String key = Version.buildVersionKey(app, namespace, environment);
        Version versionEntity = versionMapper.selectByKey(key);
        return versionEntity == null ? 0 : versionEntity.getVersion();
    }
}
