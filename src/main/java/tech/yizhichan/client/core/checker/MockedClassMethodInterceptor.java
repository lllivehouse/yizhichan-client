package tech.yizhichan.client.core.checker;

import tech.yizhichan.client.core.compiler.ClassMethodWithResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import java.util.List;

/**
 * @description: MockedClassMethodInterceptor
 * @author: lex
 * @date: 2024-09-26
 **/
@RequiredArgsConstructor
public class MockedClassMethodInterceptor extends GroovyInterceptor {

    private final List<ClassMethodWithResponse> mockedClasses;

    @Override
    public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        if (CollectionUtils.isNotEmpty(mockedClasses)) {
            for (ClassMethodWithResponse instance : mockedClasses) {
                if (StringUtils.equals(instance.getClassName(), receiver.getClass().getSimpleName())
                        && (StringUtils.isBlank(method) || instance.getMethodName().equals(method))) {
                    return instance.getResponse();
                }
            }
        }
        return super.onMethodCall(invoker, receiver, method, args);
    }
}
