package com.shuzhi.websocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * 从容器中获取bean
 *
 * @author zgk
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;
    
    @Override
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    /**
     * 通过名称获取bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String name) {
        return (T)applicationContext.getBean(name);
    }

    /**
     * 通过类型获取bean
     */
    public static <T> T get(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 判断某个bean是不是存在
     */
    public static boolean has(String name) {
        return applicationContext.containsBean(name);
    }
}
