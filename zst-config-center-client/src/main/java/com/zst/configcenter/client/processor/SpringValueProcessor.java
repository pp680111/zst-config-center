package com.zst.configcenter.client.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 负责处理@Value注解形式的配置属性注入
 * 1，扫描所有的@Value注解，保存metadata
 * 2，在配置变更时，更新所有的@Value注解对应的字段的值
 */
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
