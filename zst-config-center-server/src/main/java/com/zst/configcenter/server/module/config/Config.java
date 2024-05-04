package com.zst.configcenter.server.module.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("configs")
public class Config {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    @NotEmpty
    private String app;
    @NotEmpty
    private String namespace;
    @NotEmpty
    private String environment;
    @NotEmpty
    private String configKey;
    @NotEmpty
    private String configVal;
}
