package tech.yizhichan.sdk.generic.openfeign;

import lombok.RequiredArgsConstructor;

/**
 * @description: DynamicFeignClient
 * @author: lex
 * @date: 2024-10-02
 **/
@RequiredArgsConstructor
public class DynamicFeignClient {

    private final DynamicFeignClientFactory<DynamicFeignService> dynamicFeignClientFactory;

    public Object executePostApi(String feignClientName, String url, Object params) {
        return dynamicFeignClientFactory.getFeignClient(DynamicFeignService.class, feignClientName).executePostApi(url, params);
    }

    public Object executeGetApi(String feignClientName, String url, Object params) {
        return dynamicFeignClientFactory.getFeignClient(DynamicFeignService.class, feignClientName).executeGetApi(url, params);
    }

    public Object executePutApi(String feignClientName, String url, Object params) {
        return dynamicFeignClientFactory.getFeignClient(DynamicFeignService.class, feignClientName).executePutApi(url, params);
    }

    public Object executeDeleteApi(String feignClientName, String url, Object params) {
        return dynamicFeignClientFactory.getFeignClient(DynamicFeignService.class, feignClientName).executeDeleteApi(url, params);
    }
}
