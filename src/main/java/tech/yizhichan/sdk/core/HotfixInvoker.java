package tech.yizhichan.sdk.core;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.RandomUtil;
import tech.yizhichan.common.model.R;
import tech.yizhichan.sdk.exception.ClientErrorCodeEnum;
import tech.yizhichan.sdk.exception.ServerlessClientException;
import groovy.lang.GroovyObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @description: HotfixInvoker
 * @author: lex
 * @date: 2024-08-17
 **/
public final class HotfixInvoker implements CodeInvoker {
    private static final String HOTFIX_INVOKER_METHOD = "hotfix";

    private HotfixInvokerConfiguration configuration;

    public HotfixInvoker(HotfixInvokerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public R invoke(CodeInvokerContext context) {
        Class targetClass = configuration.getTargetClass();
        String targetMethod = configuration.getTargetMethod();
        Class<?> returnClass = context.getReturnClass();
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
        if (targetClass == null || returnClass == null || StringUtils.isAnyBlank(targetMethod, code)) {
            return R.failed(ClientErrorCodeEnum.ILLEGAL_ARGS);
        }
        DynamicType.Builder.MethodDefinition.ParameterDefinition.Initial<Object> objectInitial = new ByteBuddy()
                // 指定基类
                .subclass(Object.class)
                // 指定生成的类名
                .name(StringUtils.substringBeforeLast(targetClass.getName(), ".") + ".GeneratedInterceptor" + RandomUtil.randomNumbers(4))
                .defineMethod(targetMethod, returnClass, Visibility.PUBLIC, Ownership.STATIC);
        List<Class> argumentClasses = configuration.getArgumentClasses();
        if (CollectionUtils.isNotEmpty(argumentClasses)) {
            objectInitial.withParameters(argumentClasses.toArray(new Class[0]));
        }
        Map<String, Object> args = mergeVars(context.getEnvVars(), context.getArgs());
        MethodHandles.Lookup lookup;
        try {
            lookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Class<?> staticInterceptorClass = objectInitial.intercept(FixedValue.value(GroovyHelper.invokeMethod(groovyObject, HOTFIX_INVOKER_METHOD, args)))
                .make()
                .load(ClassLoaderUtil.getClassLoader(), ClassLoadingStrategy.UsingLookup.of(lookup))
                .getLoaded();
        new ByteBuddy()
                .redefine(targetClass)
                .method(ElementMatchers.named(targetMethod).and(ElementMatchers.returns(returnClass)))
                .intercept(MethodDelegation.to(staticInterceptorClass))
                .make()
                .load(ClassLoaderUtil.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent(ClassReloadingStrategy.Strategy.RETRANSFORMATION));
        return R.ok();
    }

    @Override
    public <V> R<Future<R<V>>> asyncInvoke(CodeInvokerContext<V> context) {
        if (!BooleanUtils.isTrue(configuration.getIsAsync()) || configuration.getThreadPoolExecutor() == null) {
            return R.failed(ClientErrorCodeEnum.ILLEGAL_ARGS);
        }
        return R.ok(configuration.getThreadPoolExecutor().submit(() -> invoke(context)));
    }
}
