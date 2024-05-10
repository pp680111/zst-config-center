package com.zst.configcenter.client.processor.model;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Member;

/**
 * 对Spring Bean中使用了@Value的代码的metadata的封装
 */
@Getter
@Setter
public class SpringValue {
    private String beanName;
    private Object bean;
    private Member target;
    private String placeholder;
    private String propertyKey;
}
