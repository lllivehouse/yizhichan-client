package tech.yizhichan.client.core.checker;

import tech.yizhichan.client.core.compiler.ClassMethod;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import java.util.List;

/**
 * @description: ForbiddenClassMethodInterceptor
 * @author: lex
 * @date: 2024-09-26
 **/
@RequiredArgsConstructor
public class ForbiddenClassMethodInterceptor extends GroovyInterceptor {

    private final List<ClassMethod> staticMethods;

    private final List<ClassMethod> instMethods;

    @Override
    public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        if (CollectionUtils.isNotEmpty(staticMethods)) {
            for (ClassMethod staticMethod : staticMethods) {
                if (StringUtils.equals(staticMethod.getClasspath(), receiver.getClass().getName())
                        && (StringUtils.isBlank(method) || staticMethod.getMethodName().equals(method))) {
                    throw new SecurityException(receiver.getClass().getName() + "." + method + "() not allowed");
                }
            }
        }
        return super.onStaticCall(invoker, receiver, method, args);
    }

    @Override
    public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        if (CollectionUtils.isNotEmpty(instMethods)) {
            for (ClassMethod instMethod : instMethods) {
                if (StringUtils.equals(instMethod.getClasspath(), receiver.getClass().getName())
                        && (StringUtils.isBlank(method) || instMethod.getMethodName().equals(method))) {
                    throw new SecurityException(receiver.getClass().getName() + "." + method + "() not allowed");
                }
            }
        }
        return super.onMethodCall(invoker, receiver, method, args);
    }
}
