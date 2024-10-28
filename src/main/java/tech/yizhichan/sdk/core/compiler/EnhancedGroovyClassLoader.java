package tech.yizhichan.sdk.core.compiler;

import tech.yizhichan.sdk.cache.GroovyClassCacheStore;
import tech.yizhichan.sdk.cache.GroovyCompilerConfigurationCacheStore;
import tech.yizhichan.sdk.exception.ClientErrorCodeEnum;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @description: EnhancedGroovyClassLoader
 * @author: lex
 * @date: 2024-10-03
 **/
@Slf4j
public class EnhancedGroovyClassLoader {

    private GroovyClassLoader groovyClassLoader;

    private EnhancedGroovyClassLoader(ClassMethodWithResponse... mockedClasses) {
        CompilerConfiguration configuration = GroovyCompilerConfigurationCacheStore.get(mockedClasses);
        if (configuration == null) {
            configuration = GroovyCompilerConfigurationDefinition.define(GroovyCompilerSetting.builder()
                    .mockedClasses(mockedClasses == null ? List.of() : Arrays.asList(mockedClasses))
                    .build());
            GroovyCompilerConfigurationCacheStore.put(mockedClasses, configuration);
        }
        this.groovyClassLoader = new GroovyClassLoader(this.getClass().getClassLoader(), configuration);
    }

    private EnhancedGroovyClassLoader(GroovyClassLoader groovyClassLoader) {
        this.groovyClassLoader = groovyClassLoader;
    }

    public static EnhancedGroovyClassLoader create(GroovyClassLoader groovyClassLoader) {
        return new EnhancedGroovyClassLoader(groovyClassLoader);
    }

    public static EnhancedGroovyClassLoader create(List<ClassMethodWithResponse> mockedClasses) {
        return new EnhancedGroovyClassLoader(CollectionUtils.isNotEmpty(mockedClasses) ? mockedClasses.toArray(new ClassMethodWithResponse[0]) : null);
    }

    public Class parseClass(String code) {
        Class<?> clazz = GroovyClassCacheStore.get(code.getBytes());
        if (clazz != null) {
            return clazz;
        }
        try (GroovyClassLoader classLoader = this.groovyClassLoader) {
            String enhancement = GroovyCompilerConfigurationDefinition.preprocessCode(code);
            log.info("转换前代码={}\n转换后代码={}", code, enhancement);
            Class klass = classLoader.parseClass(enhancement);
            GroovyClassCacheStore.put(code.getBytes(), klass);
            classLoader.clearCache();
            return klass;
        } catch (Throwable e) {
            log.error(ClientErrorCodeEnum.GROOVY_BUILD_ERROR.getMsg(), e);
            return null;
        }
    }

    public static void registerBean(String beanName, Class beanClass, ConfigurableApplicationContext applicationContext) {
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getRawBeanDefinition();
        applicationContext.getAutowireCapableBeanFactory().applyBeanPostProcessorsAfterInitialization(beanDefinition, beanName);
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public static void registerController(String controllerName, ApplicationContext applicationContext) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Object controller = applicationContext.getBean(controllerName);
        if (requestMappingHandlerMapping == null || controller == null) {
            log.error("requestMappingHandlerMapping or controller is null");
            return;
        }
        Method method;
        try {
            method = RequestMappingHandlerMapping.class.getSuperclass().getSuperclass().getDeclaredMethod("detectHandlerMethods", Object.class);
        } catch (NoSuchMethodException e) {
            log.error("detectHandlerMethods method not found", e);
            return;
        }
        //将private改为可使用
        method.setAccessible(true);
        try {
            method.invoke(requestMappingHandlerMapping, controllerName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("detectHandlerMethods method invoke error", e);
        }
    }
}
