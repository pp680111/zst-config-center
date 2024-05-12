package com.zst.configcenter.client.processor;

import com.alibaba.fastjson2.JSON;
import com.zst.configcenter.client.processor.model.SpringValue;
import com.zst.configcenter.client.processor.model.SpringValueRegistrar;
import com.zst.configcenter.client.utils.PlaceholderHelper;
import com.zst.configcenter.client.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 扫描所有Bean中的@Value注解，记录Metadata，用于后续配置值的更新
 */
@Slf4j
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<EnvironmentChangeEvent> {
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

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        if (!event.getKeys().isEmpty()) {
            log.debug("received EnvironmentChangeEvent, changedKey = " + JSON.toJSONString(event.getKeys()));
            refreshSpringValues(event.getKeys());
        }
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
            if (valueAnnotation == null) {
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

    private void refreshSpringValues(Set<String> keys) {
        List<SpringValue> list = new ArrayList<>();
        for (String changedKey : keys) {
            list.addAll(springValueRegistrar.get(changedKey));
        }

        list.forEach(springValue -> {
            Object newValue = placeholderHelper.resolvePropertyValue((ConfigurableBeanFactory) this.beanFactory,
                    springValue.getBeanName(), springValue.getPlaceholder());
            // TODO 如果后面加上了其它类型的springValue，那么这里就要加上判断
            ReflectionUtils.setFieldValue(springValue.getBean(), (Field) springValue.getTarget(), newValue);
        });
    }
}
