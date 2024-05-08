package com.zst.configcenter.client.processor;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 负责处理@Value注解形式的配置属性注入
 * 1，扫描所有的@Value注解，保存metadata
 * 2，在配置变更时，更新所有的@Value注解对应的字段的值
 */
public class SpringValueProcessor implements BeanPostProcessor {
}
