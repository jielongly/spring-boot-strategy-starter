package org.jl.strategy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StrategyServiceFactory
 * <p>
 * A central registry that automatically discovers and caches all strategy beans
 * annotated with {@link StrategyService}. It builds a fast lookup table from
 * enum constants to their corresponding strategy implementations, eliminating
 * the need for verbose switch-case statements and hard-coded mappings.
 * <p>
 * Purpose:
 * - Auto-detect strategies at application startup.
 * - Provide O(1) retrieval of a strategy by its enum key.
 * - Allow new strategies to be added without modifying any core code.
 * <p>
 * Avoids:
 * - Repetitive switch-case or if-else chains.
 * - Manual registration boilerplate.
 * - Tight coupling between enum keys and strategy implementations.
 */
@Component
public class StrategyServiceFactory implements BeanFactoryPostProcessor {

    private final Map<Class<? extends Enum<?>>, Map<Enum<?>, Object>> cache = new ConcurrentHashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
        String[] names = bf.getBeanNamesForAnnotation(StrategyService.class);
        for (String name : names) {
            Object bean = bf.getBean(name);
            if (!(bean instanceof IStrategyService)) {
                continue;
            }

            IStrategyService<?> strategy = (IStrategyService<?>) bean;
            Enum<?> key = (Enum<?>) strategy.getEnum();
            Class<? extends Enum<?>> enumType = key.getDeclaringClass();

            Map<Enum<?>, Object> map = cache.get(enumType);
            if (map == null) {
                map = new ConcurrentHashMap<>();
                cache.put(enumType, map);
            }
            map.put(key, bean);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Enum<?> key, Class<T> serviceClass) {
        Map<Enum<?>, Object> map = cache.get(key.getDeclaringClass());
        Object bean = map == null ? null : map.get(key);
        if (bean == null) {
            throw new IllegalArgumentException("Strategy not registered for key: " + key);
        }
        return serviceClass.cast(bean);
    }

}