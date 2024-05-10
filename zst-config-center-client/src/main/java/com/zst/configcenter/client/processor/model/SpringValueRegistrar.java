package com.zst.configcenter.client.processor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责记录管理SpringValue的信息
 */
public class SpringValueRegistrar {
    // key=propertyKey，value=List<SpringValue>的Map
    private final Map<String, List<SpringValue>> springValueMap = new HashMap<>();

    public void register(List<SpringValue> springValues) {
        if (springValues == null || springValues.isEmpty()) {
            return;
        }

        springValues.forEach(springValue -> {
            springValueMap.computeIfAbsent(springValue.getPropertyKey(), key -> new ArrayList<>()).add(springValue);
        });
    }

    public List<SpringValue> get(String propertyKey) {
        return springValueMap.getOrDefault(propertyKey, Collections.emptyList());
    }
}
