package tech.yizhichan.sdk.interceptor;

import tech.yizhichan.sdk.annotation.WorkflowInjection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author lex
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
@Order(100)
public class WorkflowInjectionInterceptor {

    private final DefaultParameterNameDiscoverer nameDiscoverer;

    @Around("@annotation(workflowInjection)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint joinPoint, WorkflowInjection workflowInjection) {
        Class clazz = joinPoint.getSignature().getDeclaringType();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 方法形参名
        String[] argNames = Optional.ofNullable(nameDiscoverer.getParameterNames(method)).orElse(new String[0]);
        // 方法实参值
        Object[] argValues = joinPoint.getArgs();

        Object result;
        boolean error = false;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            error = true;
            throw e;
        } finally {
            if (!error) {

            }
        }
        return result;
    }
}
