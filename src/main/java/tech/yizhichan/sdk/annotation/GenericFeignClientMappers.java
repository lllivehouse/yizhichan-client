package tech.yizhichan.sdk.annotation;

import java.lang.annotation.*;

/**
 * @author lex
 * @desc GenericFeignClientMappers
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface GenericFeignClientMappers {

    String name() default "";

    GenericFeignClientMethodMapper[] value();
}