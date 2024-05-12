package com.zst.configcenter.server.module.version;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * 配置数据版本号
 */
@Getter
@Setter
@TableName("version")
public class Version {
    @TableId(type = IdType.ASSIGN_ID)
    private String key;
    private Integer version;

    public static String buildVersionKey(String app, String namespace, String environment) {
        return MessageFormat.format("{0}_{1}_{2}", app, namespace, environment);
    }
}
