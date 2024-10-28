package tech.yizhichan.client.cache;

import tech.zhizheng.common.utils.cache.CacheStore;
import org.springframework.util.DigestUtils;

/**
 * @description: GroovyObjectCacheStore
 * @author: lex
 * @date: 2024-08-19
 **/
public class GroovyClassCacheStore {
    private static final CacheStore<String, Class<?>> GROOVY_CLASS_STORE = new CacheStore.Builder()
            // 16384
            .setCapacity(2 << 13)
            .setExpireSec(3600 * 24 * 7)
            .build();

    public static Class<?> get(byte[] scriptBytes) {
        if (scriptBytes == null || scriptBytes.length == 0) {
            return null;
        }
        String cacheKey = DigestUtils.md5DigestAsHex(scriptBytes);
        return GROOVY_CLASS_STORE.get(cacheKey);
    }

    public static void put(byte[] scriptBytes, Class<?> groovyClass) {
        if (scriptBytes == null || scriptBytes.length == 0 || groovyClass == null) {
            return;
        }
        String cacheKey = DigestUtils.md5DigestAsHex(scriptBytes);
        GROOVY_CLASS_STORE.put(cacheKey, groovyClass);
    }
}
