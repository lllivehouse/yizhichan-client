package tech.yizhichan.sdk.generic.openfeign;

import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

/**
 * @description: DynamicFeignClientFactory
 * @author: lex
 * @date: 2024-10-02
 **/
public class DynamicFeignClientFactory<T> {

    private FeignClientBuilder feignClientBuilder;

    public DynamicFeignClientFactory(ApplicationContext applicationContext) {
        this.feignClientBuilder = new FeignClientBuilder(applicationContext);
    }

    /**
     * 动态生成FeignClient代理对象
     *
     * @param clazz
     * @param clientName
     * @return
     */
    public T getFeignClient(final Class<T> clazz, String clientName) {
        return feignClientBuilder.forType(clazz, clientName).build();
    }
}