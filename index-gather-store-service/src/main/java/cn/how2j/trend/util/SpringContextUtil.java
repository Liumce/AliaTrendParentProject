package cn.how2j.trend.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


//工具类，用于获取IndexService
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private SpringContextUtil() {
        System.out.println("SpringContextUtil()");
    }

    private static ApplicationContext applicationContext;


    //设置容器上下文
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        System.out.println("applicationContext:" + applicationContext);
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
