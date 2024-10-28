package tech.yizhichan.client.core;

import tech.zhizheng.common.model.biz.serverless.FunctionTypeEnum;
import tech.yizhichan.client.exception.ServerlessClientException;

import java.util.Map;

/**
 * @description: Function
 * @author: lex
 * @date: 2024-08-17
 **/
public final class Function extends AbstractComponent {

    public Function(FunctionExecutionEngine engine) {
        super(engine);
    }

    public <V> Function(FunctionExecutionEngine.Material<V> engineMaterial) throws ServerlessClientException {
        super(new FunctionExecutionEngine(engineMaterial));
    }

    public <V> Function(FunctionTypeEnum functionType,
                                             String code,
                                             String codeVersion,
                                             Integer timeoutMillis,
                                             Integer exceptionRetryTimes,
                                             Boolean isAsync,
                                             Map<String, Object> environmentVariables,
                                             Map<String, Object> args,
                                             TracingContext trace,
                                             Class<V> returnClass) throws ServerlessClientException {
        this(FunctionExecutionEngine.Material.<V>builder()
                .functionType(functionType)
                .code(code)
                .codeVersion(codeVersion)
                .timeoutMillis(timeoutMillis)
                .exceptionRetryTimes(exceptionRetryTimes)
                .isAsync(isAsync)
                .environmentVariables(environmentVariables)
                .args(args)
                .trace(trace)
                .returnClass(returnClass)
                .build());
    }
}
