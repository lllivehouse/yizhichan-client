package tech.yizhichan.sdk.annotation;

import java.lang.annotation.*;

/**
 * @author lex
 * @desc function注射器
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunctionInjection {

    /**
     * @return
     */
    String name() default "";

    boolean isAsync() default false;

    String traceId() default "";

    String clientIp() default "";
}