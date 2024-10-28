package tech.yizhichan.client.core;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: GroovyInvokerConfiguration
 * @author: lex
 * @date: 2024-08-19
 **/
@Data
@SuperBuilder
public class GroovyInvokerConfiguration {

    private String code;
    private String codeVersion;
    private Integer timeoutMillis;
    private Integer exceptionRetryTimes;
    private Boolean isAsync;
    private ThreadPoolExecutor threadPoolExecutor;
}
