package tech.yizhichan.sdk.core;

import tech.yizhichan.common.model.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: Hotfix
 * @author: lex
 * @date: 2024-08-28
 **/
public final class Hotfix<V> {
    private HotfixInvoker invoker;
    private CodeInvokerContext<V> context;

    public Hotfix(Class<?> classToBeFixed,
                  String methodNameToBeFixed,
                  LinkedHashMap<String, Object> methodArgs,
                  Map<String, Object> envVars,
                  String fixedCode,
                  Class<V> returnClass,
                  Boolean isAsync,
                  TracingContext trace) {
        List<Class> argumentClasses = methodArgs.values().stream().filter(v -> v != null).map(v -> v.getClass()).collect(Collectors.toUnmodifiableList());
        invoker = new HotfixInvoker(HotfixInvokerConfiguration.builder()
                .code(fixedCode)
                .targetClass(classToBeFixed)
                .targetMethod(methodNameToBeFixed)
                .argumentClasses(argumentClasses)
                .isAsync(isAsync)
                .build());
        List<GroovyMethodArgument> gmrArgs = parseFromMap(methodArgs);
        List<GroovyMethodArgument> gmrEnvVars = parseFromMap(envVars);
        context = new CodeInvokerContext<>(gmrEnvVars, gmrArgs, returnClass, trace);
    }

    public R<V> start() {
        return invoker.invoke(context);
    }

    private List<GroovyMethodArgument> parseFromMap(Map<String, Object> map) {
        return map.entrySet().stream().map(e -> new GroovyMethodArgument(e.getKey(), String.valueOf(e.getValue()), e.getValue().getClass())).toList();
    }
}
