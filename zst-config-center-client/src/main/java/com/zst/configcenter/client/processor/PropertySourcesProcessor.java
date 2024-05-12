package com.zst.configcenter.client.processor;

import com.zst.configcenter.client.source.ZstPropertySource;
import com.zst.configcenter.client.remote.ZstConfigService;
import com.zst.configcenter.client.remote.ZstConfigServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于将ZstPropertySource注入到Spring Environment中的Processor
 *
 * (目前是利用BeanFactoryPostProcessor执行时间点比较早的特性，来将ZstPropertySource在Spring上下文的初始化早期阶段注入进去，
 * 以便给后面的Bean的创建提供配置属性）
 */
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, PriorityOrdered, EnvironmentAware, ApplicationContextAware {
    private static final String PROPERTY_SOURCE_NAME = "zstPropertySource";
    private static final String COMPOSITE_PROPERTY_SOURCE_NAME = "zstCompositePropertySource";

    Environment environment;
    ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        if (!env.getPropertySources().contains(PROPERTY_SOURCE_NAME)) {
            // TODO 通过http从config-server获取配置信息，构建ConfigService

            Map<String, String> tmpConfigMap = new HashMap<>();
            tmpConfigMap.put("zst.aa", "a01");
            tmpConfigMap.put("zst.bb", "b01");

            // 下面这段代码，单独放入ZstPropertySource和放入CompositePropertySource有什么区别，目前暂时没搞懂
            ZstConfigService configService = ZstConfigServiceFactory.create(applicationContext, env);
            ZstPropertySource zstPropertySource = new ZstPropertySource(PROPERTY_SOURCE_NAME, configService);
//            CompositePropertySource compositePropertySource = new CompositePropertySource(COMPOSITE_PROPERTY_SOURCE_NAME);
//            compositePropertySource.addPropertySource(zstPropertySource);
//            env.getPropertySources().addFirst(compositePropertySource);
            env.getPropertySources().addFirst(zstPropertySource);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
