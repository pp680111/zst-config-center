package com.zst.configcenter.client.processor;

import com.zst.configcenter.client.processor.model.SpringValue;
import com.zst.configcenter.client.processor.model.SpringValueRegistrar;
import com.zst.configcenter.client.utils.PlaceholderHelper;
import com.zst.configcenter.client.utils.ReflectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 负责处理@Value注解形式的配置属性注入
 * 1，扫描所有的@Value注解，保存metadata
 * 2，在配置变更时，更新所有的@Value注解对应的字段的值
 */
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware {
    private final PlaceholderHelper placeholderHelper = new PlaceholderHelper();
    private final SpringValueRegistrar springValueRegistrar = new SpringValueRegistrar();
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 这里貌似扫不到启动类，要检查一下为什么
        scanForSpringValue(bean, beanName);

        return bean;
    }

    private void scanForSpringValue(Object bean, String beanName) {
        List<SpringValue> fieldValues = scanField(bean, beanName);
        if (!fieldValues.isEmpty()) {
            springValueRegistrar.register(fieldValues);
        }

        // TODO 对加在Setter方法上的@Value注解进行处理
    }

    /**
     * 扫描目标对象中所有使用了@Value注解的字段
     * @param bean
     * @param beanName
     * @return
     */
    private List<SpringValue> scanField(Object bean, String beanName) {
        List<Field> targetField = ReflectionUtils.getAnnotatedFields(bean.getClass(), Value.class);
        if (targetField == null || targetField.isEmpty()) {
            return Collections.emptyList();
        }

        List<SpringValue> result = new ArrayList<>();
        for (Field field : targetField) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (valueAnnotation != null) {
                throw new RuntimeException("cannot found Value annotation on field " + field.toString());
            }

            if (!StringUtils.hasLength(valueAnnotation.value())) {
                continue;
            }

            Set<String> placeholderKeys = placeholderHelper.extractPlaceholderKeys(valueAnnotation.value());
            for (String key : placeholderKeys) {
                SpringValue springValue = new SpringValue();
                springValue.setBeanName(beanName);
                springValue.setBean(bean);
                springValue.setTarget(field);
                springValue.setPlaceholder(valueAnnotation.value());
                springValue.setPropertyKey(key);
                result.add(springValue);
            }
        }

        return result;
    }

}
