package tech.yizhichan.sdk.annotation;

import tech.yizhichan.sdk.config.ServerlessAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lex
 * @createTime 2024/08/21
 * @description EnableServerless
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ServerlessAutoConfiguration.class})
public @interface EnableServerless {
}