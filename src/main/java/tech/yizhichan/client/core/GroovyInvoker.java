package tech.yizhichan.client.core;

import tech.zhizheng.common.model.R;
import tech.zhizheng.common.model.exception.CommonErrorCodeEnum;
import tech.yizhichan.client.exception.ClientErrorCodeEnum;
import tech.yizhichan.client.exception.ServerlessClientException;
import groovy.lang.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.groovy.parser.antlr4.GroovySyntaxError;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.metaclass.MissingPropertyExceptionNoStack;
import org.codehaus.groovy.runtime.typehandling.GroovyCastException;
import org.codehaus.groovy.syntax.RuntimeParserException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: GroovyInvoker
 * @author: lex
 * @date: 2024-08-17
 * @description: 约定每个groovy类的方法名必须有accept, onBefore, action, onException, onAfter，且约定每个方法的入参为Map对象
 **/
@Slf4j
public final class GroovyInvoker implements CodeInvoker {
    private static final String[] METHOD_ARRAY = {"accept", "onBefore", "action", "onException", "onAfter"};

    private GroovyInvokerConfiguration configuration;

    public GroovyInvoker(GroovyInvokerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <V> R<V> invoke(CodeInvokerContext<V> context) {
        String code = configuration.getCode();
        if (StringUtils.isBlank(code)) {
            return R.failed(ClientErrorCodeEnum.ILLEGAL_ARGS);
        }
        GroovyObject groovyObject;
        try {
            groovyObject = GroovyHelper.loadCode(code);
        } catch (ServerlessClientException e) {
            return R.failed(e.getCode(), e.getMessage());
        }
        Map<String, Object> inputVars = mergeVars(context.getEnvVars(), context.getArgs());
        return callProcess(groovyObject, inputVars,
                obj -> {
                    Object o = GroovyHelper.invokeMethod(obj, METHOD_ARRAY[0], inputVars);
                    return o instanceof Boolean ? (Boolean) o : false;
                }, METHOD_ARRAY[0],
                obj -> GroovyHelper.invokeMethod(obj, METHOD_ARRAY[1], inputVars), METHOD_ARRAY[1],
                obj -> (V) GroovyHelper.retryMethod(obj, METHOD_ARRAY[2], inputVars, configuration.getExceptionRetryTimes()), METHOD_ARRAY[2],
                obj -> GroovyHelper.invokeMethod(obj, METHOD_ARRAY[3], inputVars), METHOD_ARRAY[3],
                obj -> GroovyHelper.invokeMethod(obj, METHOD_ARRAY[4], inputVars), METHOD_ARRAY[4]);
    }

    @Override
    public <V> R<Future<R<V>>> asyncInvoke(CodeInvokerContext<V> context) {
        if (!BooleanUtils.isTrue(configuration.getIsAsync()) || configuration.getThreadPoolExecutor() == null) {
            return R.failed(ClientErrorCodeEnum.ILLEGAL_ARGS);
        }
        return R.ok(configuration.getThreadPoolExecutor().submit(() -> invoke(context)));
    }

    private <T> R<T> funcWithTryCatch(GroovyObject groovyObject, Function<GroovyObject, T> func, String methodName, Map<String, Object> argsMap) {
        try {
            return R.ok(func.apply(groovyObject));
        } catch (CompilationFailedException | GroovySyntaxError | GroovyCastException e) {
            log.error("error to compile script,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_BUILD_ERROR, e.getMessage());
        } catch (InvokerInvocationException e) {
            log.error("error to invoke script,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_INVOKER_TIMEOUT_ERROR, e.getMessage());
        } catch (RuntimeParserException e) {
            log.error("error to parse script,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_RUNTIME_PARSER_ERROR, e.getMessage());
        } catch (MissingClassException e) {
            log.error("groovy missing class error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_MISSING_CLASS_ERROR, e.getMessage());
        } catch (MissingMethodException e) {
            log.error("groovy missing method error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_MISSING_METHOD_ERROR, e.getMessage());
        } catch (IllegalPropertyAccessException | MissingPropertyExceptionNoStack | ReadOnlyPropertyException e) {
            log.error("groovy illegal property error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_ILLEGAL_PROPERTY_ERROR, e.getMessage());
        } catch (MissingFieldException e) {
            log.error("groovy missing field error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_MISSING_FIELD_ERROR, e.getMessage());
        } catch (IncorrectClosureArgumentsException e) {
            log.error("groovy illegal property error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_INCORRECT_CLOSURE_ARGS_ERROR, e.getMessage());
        } catch (GroovyRuntimeException e) {
            log.error("groovy runtime error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_RUNTIME_ERROR, e.getMessage());
        } catch (Throwable e) {
            log.error("system error to call method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(CommonErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        }
    }

    private R<Void> consumerWithTryCatch(GroovyObject groovyObject, Consumer<GroovyObject> consumer, String methodName, Map<String, Object> argsMap) {
        try {
            consumer.accept(groovyObject);
            return R.ok();
        } catch (CompilationFailedException | GroovySyntaxError | GroovyCastException e) {
            log.error("error to compile script,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_BUILD_ERROR, e.getMessage());
        } catch (InvokerInvocationException e) {
            log.error("error to invoke script,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_INVOKER_TIMEOUT_ERROR, e.getMessage());
        } catch (RuntimeParserException e) {
            log.error("error to parse script,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_RUNTIME_PARSER_ERROR, e.getMessage());
        } catch (MissingClassException e) {
            log.error("groovy missing class error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_MISSING_CLASS_ERROR, e.getMessage());
        } catch (MissingMethodException e) {
            log.error("groovy missing method error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_MISSING_METHOD_ERROR, e.getMessage());
        } catch (IllegalPropertyAccessException | MissingPropertyExceptionNoStack | ReadOnlyPropertyException e) {
            log.error("groovy illegal property error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_ILLEGAL_PROPERTY_ERROR, e.getMessage());
        } catch (MissingFieldException e) {
            log.error("groovy missing field error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_MISSING_FIELD_ERROR, e.getMessage());
        } catch (IncorrectClosureArgumentsException e) {
            log.error("groovy illegal property error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_INCORRECT_CLOSURE_ARGS_ERROR, e.getMessage());
        } catch (GroovyRuntimeException e) {
            log.error("groovy runtime error,method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(ClientErrorCodeEnum.GROOVY_RUNTIME_ERROR, e.getMessage());
        } catch (Throwable e) {
            log.error("system error to call method={},args={}",
                    methodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            return R.failed(CommonErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        }
    }

    private <V> R<V> callProcess(GroovyObject groovyObject, Map<String, Object> argsMap,
                                 Function<GroovyObject, Boolean> acceptHandler, String acceptMethodName,
                                 Consumer<GroovyObject> onBeforeHandler, String onBeforeMethodName,
                                 Function<GroovyObject, V> actionHandler, String actionMethodName,
                                 Consumer<GroovyObject> onErrorHandler, String onErrorMethodName,
                                 Consumer<GroovyObject> onSuccessHandler, String onSuccessMethodName) {

        R<Boolean> predictR = funcWithTryCatch(groovyObject, obj -> acceptHandler.apply(obj), acceptMethodName, argsMap);
        if (!Boolean.TRUE.equals(predictR.getData())) {
            String msg = predictR.getMsg();
            if (StringUtils.isNotBlank(msg)) {
                return R.result(null, predictR.getCode(), msg);
            }
            return R.failed(ClientErrorCodeEnum.PRECONDITION_NOT_MATCHED);
        }
        consumerWithTryCatch(groovyObject, obj -> onBeforeHandler.accept(obj), onBeforeMethodName, argsMap);
        try {
            V v = actionHandler.apply(groovyObject);
            return v instanceof R ? (R) v : R.ok(v);
        } catch (Throwable e) {
            log.error("error to execute script,method={},args={}",
                    actionMethodName,
                    Optional.ofNullable(argsMap).orElse(new HashMap<>()).entrySet().stream().map(en -> en.getKey() + ":" + en.getValue()).collect(Collectors.joining(",")),
                    e);
            consumerWithTryCatch(groovyObject, obj -> onErrorHandler.accept(obj), onErrorMethodName, argsMap);
            return R.failed(ClientErrorCodeEnum.GROOVY_RUNTIME_ERROR, e.getMessage());
        } finally {
            consumerWithTryCatch(groovyObject, obj -> onSuccessHandler.accept(obj), onSuccessMethodName, argsMap);
        }
    }

}
