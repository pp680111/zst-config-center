package com.zst.configcenter.server.module.clusterlock;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClusterLockMapper {
    /**
     * 尝试占据锁记录
     * @param instanceId
     * @param timeout
     * @return
     */
    int tryOccupyLock(@Param("instanceId") String instanceId, @Param("timeout") String timeout);
}
