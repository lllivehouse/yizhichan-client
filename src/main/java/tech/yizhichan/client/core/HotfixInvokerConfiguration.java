package tech.yizhichan.client.core;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: GroovyInvokerConfiguration
 * @author: lex
 * @date: 2024-08-19
 **/
@Data
@SuperBuilder
public class HotfixInvokerConfiguration {

    private String code;
    private Class targetClass;
    private String targetMethod;
    private List<Class> argumentClasses;
    private Boolean isAsync;
    private ThreadPoolExecutor threadPoolExecutor;
}
