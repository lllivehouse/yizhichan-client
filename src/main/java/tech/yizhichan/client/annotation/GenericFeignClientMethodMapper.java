package tech.yizhichan.client.annotation;

import java.lang.annotation.*;

/**
 * @author lex
 * @desc GenericFeignClientMethodMapper
 */
@Repeatable(GenericFeignClientMappers.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface GenericFeignClientMethodMapper {

    String sourceMethodName();

    String targetMethodUrl();

    String httpMethod() default "post";
}