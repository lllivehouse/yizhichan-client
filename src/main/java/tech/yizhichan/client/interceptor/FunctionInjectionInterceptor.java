package tech.yizhichan.client.interceptor;

import cn.hutool.extra.spring.SpringUtil;
import tech.zhizheng.common.model.R;
import tech.zhizheng.common.utils.GsonFactory;
import tech.yizhichan.client.annotation.FunctionInjection;
import tech.yizhichan.client.apiclient.v1.DataFetchClient;
import tech.yizhichan.client.apiclient.v1.GetFunctionMetadataResponse;
import tech.yizhichan.client.config.ServerlessProperties;
import tech.yizhichan.client.convertor.ObjectMapper;
import tech.yizhichan.client.core.Function;
import tech.yizhichan.client.core.FunctionExecutionEngine;
import tech.yizhichan.client.core.TracingContext;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author lex
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
@Order(100)
public class FunctionInjectionInterceptor {

    private final DefaultParameterNameDiscoverer nameDiscoverer;

    @Around("@annotation(functionInjection)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint joinPoint, FunctionInjection functionInjection) {
        Class clazz = joinPoint.getSignature().getDeclaringType();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 方法形参名
        String[] argNames = Optional.ofNullable(nameDiscoverer.getParameterNames(method)).orElse(new String[0]);
        // 方法实参值
        Object[] argValues = joinPoint.getArgs();
        GetFunctionMetadataResponse.FunctionVO function = new GetFunctionMetadataResponse.FunctionVO();
        Map<String, Object> argMap = new HashMap<>();
        Map<String, Object> envarMap = new HashMap<>();
        TracingContext tracingContext = new TracingContext();
        try {
            String functionName = functionInjection.name();
            function = DataFetchClient.getAppliedFunctionByName(functionName).getData();
            if (function == null || StringUtils.isBlank(function.getCode())) {
                // 如果没有找到function或没有配置代码，则按照原方法执行
                return joinPoint.proceed();
            }
            ServerlessProperties serverlessProperties = SpringUtil.getBean(ServerlessProperties.class);
            String namespace = serverlessProperties.getNamespace();
            String appname = serverlessProperties.getAppname();
            Integer retryTimes = serverlessProperties.getRetryTimes();
            boolean isAsync = functionInjection.isAsync();
            tracingContext = initTracingContext(namespace, appname, functionInjection.traceId(), functionInjection.clientIp());
            argMap = toArgMap(argNames, argValues);
            envarMap = DataFetchClient.getEnvarMap();

            FunctionExecutionEngine.Material material = ObjectMapper.INSTANCE.toFunctionExecutionEngineMaterial(
                    function,
                    StringUtils.isBlank(function.getClasspath()) ? clazz : Class.forName(function.getClasspath()),
                    retryTimes,
                    argMap,
                    isAsync,
                    envarMap,
                    tracingContext);
            R newResult = new Function(material).start();
            log.info("FunctionInjectionInterceptor success,result={},function={},tracingContext={},envVars={},methodArgs={}",
                    GsonFactory.toJson(newResult),
                    GsonFactory.toJson(function),
                    GsonFactory.toJson(tracingContext),
                    GsonFactory.toJson(envarMap),
                    GsonFactory.toJson(argMap));
            return method.getReturnType().equals(newResult.getClass()) ? newResult : newResult.getData();
        } catch (Throwable e) {
            log.error("FunctionInjectionInterceptor error,function={},tracingContext={},envVars={},methodArgs={}",
                    GsonFactory.toJson(function),
                    GsonFactory.toJson(tracingContext),
                    GsonFactory.toJson(envarMap),
                    GsonFactory.toJson(argMap),
                    e);
            throw e;
        }
    }

    private Map<String, Object> toArgMap(String[] argNames, Object[] argValues) {
        if (argNames == null || argValues == null || argNames.length != argValues.length) {
            return Maps.newHashMap();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < argNames.length; i++) {
            String k = argNames[i];
            Object v = argValues[i];
            map.put(k, v);
        }
        return map;
    }

    private TracingContext initTracingContext(String namespace, String appname, String traceId, String clientIp) {
        return TracingContext.builder()
                .namespace(namespace)
                .appname(appname)
                .clientIp(clientIp)
                .traceId(Optional.ofNullable(traceId).orElse(String.join("-", namespace, appname, String.valueOf(System.currentTimeMillis()))))
                .build();
    }
}
