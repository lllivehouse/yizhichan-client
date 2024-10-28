package tech.yizhichan.sdk.core;

import tech.yizhichan.common.model.R;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @description: CodeInvoker
 * @author: lex
 * @date: 2024-08-17
 **/
public interface CodeInvoker {

    <V> R<V> invoke(CodeInvokerContext<V> context);

    <V> R<Future<R<V>>> asyncInvoke(CodeInvokerContext<V> context);

    default Map<String, Object> mergeVars(List<GroovyMethodArgument> envVars, List<GroovyMethodArgument> args) {
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(envVars)) {
            map = envVars.stream().collect(Collectors.toMap(GroovyMethodArgument::getName, GroovyMethodArgument::getValue));
        }
        if (CollectionUtils.isNotEmpty(args)) {
            map.putAll(args.stream().collect(Collectors.toMap(GroovyMethodArgument::getName, GroovyMethodArgument::getValue)));
        }
        return org.springframework.util.CollectionUtils.isEmpty(map) ? null : map;
    }
}