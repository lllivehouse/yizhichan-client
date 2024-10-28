package tech.yizhichan.sdk.cache;

import tech.yizhichan.common.utils.cache.CacheStore;
import tech.yizhichan.sdk.apiclient.v1.ListRestrictionResponse;
import groovy.lang.GroovyObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * @description: RestrictionClassCacheStore
 * @author: lex
 * @date: 2024-08-19
 **/
public class RestrictionClassCacheStore {
    private static final String B_KEY = "blacklist";
    private static final String W_KEY = "whitelist";
    private static final CacheStore<String, List<ListRestrictionResponse.RestrictionClassVO>> CACHE_STORE = new CacheStore.Builder()
            // 16384
            .setCapacity(2 << 13)
            .setExpireSec(3600 * 24 * 7)
            .build();

    public static List<ListRestrictionResponse.RestrictionClassVO> getBlacklist() {
        return CACHE_STORE.get(B_KEY);
    }

    public static List<ListRestrictionResponse.RestrictionClassVO> getWhitelist() {
        return CACHE_STORE.get(W_KEY);
    }

    public static void putBlacklist(List<ListRestrictionResponse.RestrictionClassVO> restrictionClasses) {
        if (CollectionUtils.isEmpty(restrictionClasses)) {
            return;
        }
        CACHE_STORE.put(B_KEY, restrictionClasses);
    }

    public static void putWhitelist(List<ListRestrictionResponse.RestrictionClassVO> restrictionClasses) {
        if (CollectionUtils.isEmpty(restrictionClasses)) {
            return;
        }
        CACHE_STORE.put(W_KEY, restrictionClasses);
    }
}
