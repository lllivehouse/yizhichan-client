package tech.yizhichan.sdk.core.compiler;

import cn.hutool.extra.spring.SpringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * @description: DynamicPackageScanner
 * @author: lex
 * @date: 2024-10-02
 **/
public class DynamicPackageScanner implements BeanDefinitionRegistryPostProcessor {

    private static final String[] CODE_BASE_PACKAGES = new String[]{"tech.yizhichan.code"};

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 创建一个 ClassPathBeanDefinitionScanner，用于动态扫描包
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        // 扫描动态包
        scanner.scan(CODE_BASE_PACKAGES);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
