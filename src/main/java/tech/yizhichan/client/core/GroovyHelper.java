package tech.yizhichan.client.core;

import tech.yizhichan.client.core.compiler.EnhancedGroovyClassLoader;
import tech.yizhichan.client.exception.ClientErrorCodeEnum;
import tech.yizhichan.client.exception.ServerlessClientException;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @description: GroovyHelper
 * @author: lex
 * @date: 2024-08-21
 **/
@Slf4j
public class GroovyHelper {

    public static GroovyObject loadCode(String code) throws ServerlessClientException {
        return loadScript(code);
    }

    public static Object invokeMethod(String code, String methodName, Map<String, Object> args) throws ServerlessClientException {
        GroovyObject groovyObject = loadScript(code);
        return groovyObject.invokeMethod(methodName, args);
    }

    public static Object invokeMethod(GroovyObject groovyObject, String methodName, Map<String, Object> args) {
        return groovyObject.invokeMethod(methodName, args);
    }

    public static Object retryMethod(GroovyObject groovyObject, String methodName, Map<String, Object> args, Integer retryTimes) {
        if (retryTimes == null || retryTimes == 0) {
            retryTimes = 1;
        }
        for (int i = 1; i <= retryTimes; i++) {
            try {
                Object returnValue = invokeMethod(groovyObject, methodName, args);
                return returnValue;
            } catch (Throwable e) {
                log.error("{} times: error to call method {}", i, methodName, e);
                if (i == retryTimes) {
                    throw e;
                }
            }
        }
        return null;
    }

    /**
     * 加载脚本
     *
     * @param script
     * @return
     */
    private static GroovyObject loadScript(String script) throws ServerlessClientException {
        if (StringUtils.isBlank(script)) {
            throw new ServerlessClientException("script is empty");
        }
        Class groovyClass = EnhancedGroovyClassLoader.create(List.of()).parseClass(script);
        try {
            return (GroovyObject) groovyClass.newInstance();
        } catch (Exception e) {
            throw new ServerlessClientException(ClientErrorCodeEnum.GROOVY_BUILD_ERROR, e);
        }
    }
}
