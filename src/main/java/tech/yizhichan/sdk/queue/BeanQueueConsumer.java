package tech.yizhichan.sdk.queue;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import tech.yizhichan.common.model.biz.serverless.BeanCategoryEnum;
import tech.yizhichan.common.utils.queue.AbstractDisruptorWorkConsumer;
import tech.yizhichan.sdk.cache.GroovyClassCacheStore;
import tech.yizhichan.sdk.core.compiler.ClassMethodWithResponse;
import tech.yizhichan.sdk.core.compiler.EnhancedGroovyClassLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author lex
 * @createTime 2024/9/25
 * @description BeanQueueConsumer
 */
@Slf4j
@RequiredArgsConstructor
public class BeanQueueConsumer extends AbstractDisruptorWorkConsumer<BeanProcessParameter> {

    @Override
    public void consume(BeanProcessParameter parameter) {
        log.info("BeanQueueConsumer consume: {}", parameter);
        String code = parameter.getCode();
        String beanName = parameter.getBeanName();
        if (StringUtils.isBlank(code) || GroovyClassCacheStore.get(code.getBytes()) != null) {
            log.info("BeanQueueConsumer consume code is blank or already loaded, beanName={},code={}", beanName, code);
            return;
        }
        List<ClassMethodWithResponse> mockedClasses = parameter.getApiMocked().intValue() == 1 ? List.of(
                ClassMethodWithResponse.builder()
                        .className(Optional.ofNullable(StrUtil.subBetween(code, "class ", "{")).orElse(StringUtils.EMPTY).trim())
                        .response(parameter.getResponseBody())
                        .build()) : null;
        Class clazz = EnhancedGroovyClassLoader.create(mockedClasses).parseClass(code);
        String classname = StringUtils.substringBetween(code, "class ", "{").trim();
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        EnhancedGroovyClassLoader.registerBean(classname, clazz, (ConfigurableApplicationContext) applicationContext);
        if (Arrays.asList(BeanCategoryEnum.WEBAPI, BeanCategoryEnum.INNER_API).contains(BeanCategoryEnum.getById(parameter.getCategoryId()))) {
            EnhancedGroovyClassLoader.registerController(classname, applicationContext);
        }
    }
}
