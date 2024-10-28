package tech.yizhichan.sdk.generic.openfeign;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

/**
 * @description: DynamicFeignService
 * @author: lex
 * @date: 2024-10-02
 **/
public interface DynamicFeignService {

    @PostMapping("{url}")
    Object executePostApi(@PathVariable("url") String url, @RequestBody Object params);

    @GetMapping("{url}")
    Object executeGetApi(@PathVariable("url") String url, @SpringQueryMap Object params);

    @PutMapping("{url}")
    Object executePutApi(@PathVariable("url") String url, @RequestBody Object params);

    @DeleteMapping("{url}")
    Object executeDeleteApi(@PathVariable("url") String url, @RequestBody Object params);
}
