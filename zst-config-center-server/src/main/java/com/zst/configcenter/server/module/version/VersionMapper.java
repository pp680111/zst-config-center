package com.zst.configcenter.server.module.version;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VersionMapper extends BaseMapper<Version> {
    @Update("INSERT INTO `version` (`key`, `version`) VALUES(#{key}, 0) ON DUPLICATE KEY UPDATE `version` = `version` + 1")
    int updateVersion(String key);

    @Select("SELECT * FROM `version` WHERE `key` = #{key}")
    Version selectByKey(@Param("key") String key);
}
