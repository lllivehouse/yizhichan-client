package tech.yizhichan.client.queue;

import tech.zhizheng.common.utils.biz.MethodArgumentHelper;
import tech.zhizheng.common.utils.queue.AbstractDisruptorWorkConsumer;
import tech.yizhichan.client.apiclient.v1.DataFetchClient;
import tech.yizhichan.client.core.Hotfix;
import tech.yizhichan.client.core.TracingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lex
 * @createTime 2024/8/31
 * @description HotfixQueueConsumer
 */
@Slf4j
@RequiredArgsConstructor
public class HotfixQueueConsumer extends AbstractDisruptorWorkConsumer<HotfixProcessParameter> {

    @Override
    public void consume(HotfixProcessParameter parameter) {
        log.info("HotfixQueueConsumer consume: {}", parameter);
        try {
            Class<?> clazzToBeFixed = Class.forName(parameter.getClasspath());
            Class<?> returnType = Class.forName(parameter.getReturnType());
            String methodName = parameter.getMethodName();
            String code = parameter.getCode();
            LinkedHashMap<String, Object> args = MethodArgumentHelper.parseArgs(parameter.getArgNames(), parameter.getArgValues(), parameter.getArgTypes());
            Map<String, Object> envarMap = DataFetchClient.getEnvarMap();
            new Hotfix(
                    clazzToBeFixed,
                    methodName,
                    args,
                    envarMap,
                    code,
                    returnType,
                    false,
                    initTracingContext(parameter.getNamespace(), parameter.getAppname())
            ).start();
        } catch (Throwable e) {
            log.error("HotfixQueueConsumer consume error", e);
        }
    }

    private TracingContext initTracingContext(String namespace, String appname) {
        return TracingContext.builder()
                .namespace(namespace)
                .appname(appname)
                .traceId(String.join("-", namespace, appname, String.valueOf(System.currentTimeMillis())))
                .build();
    }
}
