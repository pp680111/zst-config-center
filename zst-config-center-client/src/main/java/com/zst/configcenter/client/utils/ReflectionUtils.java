package com.zst.configcenter.client.utils;

import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static Field[] getFields(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        return getFields(o.getClass());
    }

    public static Field[] getFields(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }

        return clazz.getDeclaredFields();
    }

    public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }

        Field[] fields = getFields(clazz);
        List<Field> result = new ArrayList<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(annotationClass)) {
                result.add(f);
            }
        }

        return result;
    }

    public static <T> T getFieldAnnotation(Object ref, Field field, Class<? extends Annotation> annotationClass) {
        if (ref == null || field == null || annotationClass == null) {
            throw new IllegalArgumentException();
        }

        return (T) field.getAnnotation(annotationClass);
    }

    public static void setFieldValue(Object ref, Field field, Object value) {
        if (ref == null) {
            throw new IllegalArgumentException();
        }
        if (field == null) {
            throw new IllegalArgumentException();
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(ref, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查找指定类中的公开方法
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (clazz == null || !StringUtils.hasLength(methodName)) {
            throw new IllegalArgumentException();
        }

        try {
            // 如果没有对应方法的话，一定会跑出异常，因此不判断null
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
