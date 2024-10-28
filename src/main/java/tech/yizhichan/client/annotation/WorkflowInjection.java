package tech.yizhichan.client.annotation;

import java.lang.annotation.*;

/**
 * @author lex
 * @desc Workflow注射器
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WorkflowInjection {

}