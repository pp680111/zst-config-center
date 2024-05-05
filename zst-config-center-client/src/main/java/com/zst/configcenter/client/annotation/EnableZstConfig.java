package com.zst.configcenter.client.annotation;

import com.zst.configcenter.client.registrar.ConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 引入ZST配置中心的标记注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import(ConfigRegistrar.class)
public @interface EnableZstConfig {
}
