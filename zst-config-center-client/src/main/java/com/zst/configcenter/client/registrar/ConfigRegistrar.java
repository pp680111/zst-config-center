package com.zst.configcenter.client.registrar;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ConfigRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 判断BeanDefinition是否已经被注册，从registry中拿bd的时候如果报错了，说明还没注册，反之则是注册了，直接返回
        try {
            registry.getBeanDefinition(PropertySourcesProcessor.class.getName());
            return;
        } catch (Exception e) {
        }

        AbstractBeanDefinition abd = BeanDefinitionBuilder.genericBeanDefinition(PropertySourcesProcessor.class)
                .getBeanDefinition();
        registry.registerBeanDefinition(PropertySourcesProcessor.class.getName(), abd);
    }
}