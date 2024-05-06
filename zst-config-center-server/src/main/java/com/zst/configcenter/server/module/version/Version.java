package com.zst.configcenter.server.module.version;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

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
}
