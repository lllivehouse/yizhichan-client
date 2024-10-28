package tech.yizhichan.client.cache;

import tech.zhizheng.common.utils.cache.CacheStore;
import tech.yizhichan.client.core.compiler.ClassMethodWithResponse;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

/**
 * @description: GroovyCompilerConfigurationCacheStore
 * @author: lex
 * @date: 2024-08-19
 **/
public class GroovyCompilerConfigurationCacheStore {
    private static final CacheStore<String, CompilerConfiguration> GROOVY_COMPILER_CONFIG_STORE = new CacheStore.Builder()
            // 16384
            .setCapacity(2 << 13)
            .setExpireSec(3600 * 24 * 7)
            .build();

    public static CompilerConfiguration get(ClassMethodWithResponse[] mockedClasses) {
        String cacheKey = serializeMockedClasses(mockedClasses);
        return GROOVY_COMPILER_CONFIG_STORE.get(cacheKey);
    }

    public static void put(ClassMethodWithResponse[] mockedClasses, CompilerConfiguration compilerConfiguration) {
        String cacheKey = serializeMockedClasses(mockedClasses);
        GROOVY_COMPILER_CONFIG_STORE.put(cacheKey, compilerConfiguration);
    }

    private static String serializeMockedClasses(ClassMethodWithResponse[] mockedClasses) {
        if (mockedClasses == null || mockedClasses.length == 0) {
            return "ClassMethodWithResponse";
        }
        String body = Arrays.asList(mockedClasses).stream().map(mockedClass -> mockedClass.toString()).reduce((a, b) -> a + b).get();
        String cacheKey = DigestUtils.md5DigestAsHex(body.getBytes());
        return cacheKey;
    }
}
