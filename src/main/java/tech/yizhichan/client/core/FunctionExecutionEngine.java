package tech.yizhichan.client.core;

import tech.zhizheng.common.model.R;
import tech.zhizheng.common.model.biz.serverless.FunctionTypeEnum;
import tech.yizhichan.client.convertor.ObjectMapper;
import tech.yizhichan.client.exception.ClientErrorCodeEnum;
import tech.yizhichan.client.exception.ServerlessClientException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * @description: FunctionExecutionEngine
 * @author: lex
 * @date: 2024-08-17
 **/
public final class FunctionExecutionEngine<V> implements ExecutionEngine {
    private CodeInvoker codeInvoker;
    private CodeInvokerContext<V> context;
    private Boolean isAsync;

    public FunctionExecutionEngine(Material<V> material) throws ServerlessClientException {
        Pair<CodeInvoker, CodeInvokerContext> initialized = init(material);
        this.codeInvoker = initialized.getLeft();
        this.context = initialized.getRight();
    }

    @Override
    public R exec() {
        if (BooleanUtils.isTrue(this.isAsync)) {
            return this.codeInvoker.asyncInvoke(context);
        }
        return this.codeInvoker.invoke(context);
    }

    private Pair<CodeInvoker, CodeInvokerContext> init(Material<V> material) throws ServerlessClientException {
        FunctionTypeEnum functionType = material.getFunctionType();
        switch (functionType) {
            case GROOVY -> {
                this.isAsync = material.getIsAsync();
                return Pair.of(
                        new GroovyInvoker(ObjectMapper.INSTANCE.toGroovyInvokerConfiguration(material)),
                        ObjectMapper.INSTANCE.toCodeInvokerContext(material)
                );
            }
            default -> throw new ServerlessClientException(ClientErrorCodeEnum.ILLEGAL_ARGS);
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class Material<V> implements Serializable {
        @Serial
        private static final long serialVersionUID = 2554620273996606509L;
        private FunctionTypeEnum functionType;
        private String code;
        private String codeVersion;
        private Integer timeoutMillis;
        private Integer exceptionRetryTimes;
        private Boolean isAsync;
        private Map<String, Object> environmentVariables;
        private Map<String, Object> args;
        private TracingContext trace;
        private Class<V> returnClass;
    }
}
