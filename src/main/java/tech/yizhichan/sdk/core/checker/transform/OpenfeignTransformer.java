package tech.yizhichan.sdk.core.checker.transform;

import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import tech.yizhichan.sdk.config.ServerlessProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: OpenfeignTransformer
 * @author: lex
 * @date: 2024-10-02
 **/
@Component
@RequiredArgsConstructor
public class OpenfeignTransformer implements GroovyCodeTransformer {

    private final ServerlessProperties serverlessProperties;

    @Override
    public boolean detect(String code) {
        if (!"apaas-serverless-executor".equals(serverlessProperties.getAppname())) {
            return false;
        }
        return StringUtils.containsAny(code, "GenericFeignClientMappers", "GenericFeignClientMethodMapper");
    }

    @Override
    public String transform(String code) {
        String str = StringUtils.substringAfter(code, "GenericFeignClientMappers(");
        String feignClientName = resolveContent(str, Arrays.asList("name = \"", "name=\"", "name= \"", "name =\""), "\"");
        if (StringUtils.isBlank(feignClientName)) {
            return code;
        }
        List<String> feignApiMethods = resolveContents(str, Arrays.asList("sourceMethodName = \"", "sourceMethodName=\"", "sourceMethodName= \"", "sourceMethodName =\""), "\"");
        if (CollectionUtils.isEmpty(feignApiMethods)) {
            return code;
        }
        String feignClientPropertyName = StringUtils.substringBetween(str, StringUtils.SPACE, StrUtil.DOT + feignApiMethods.get(0) + "(");
        List<String> feignApiUrls = resolveContents(str, Arrays.asList("targetMethodUrl = \"", "targetMethodUrl=\"", "targetMethodUrl= \"", "targetMethodUrl =\""), "\"");
        List<String> feignApiHttpMethods = resolveContents(str, Arrays.asList("httpMethod = \"", "httpMethod=\"", "httpMethod= \"", "httpMethod =\""), "\"");
        String[] lines = StringUtils.split(code, "\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Integer index = getIndexIfContains(line, feignApiMethods);
            if (index != null) {
                String method = feignApiMethods.get(index);
                String url = feignApiUrls.get(index);
                String httpMethod = feignApiHttpMethods.get(index);
                line = StringUtils.replace(line, feignClientPropertyName + StrUtil.DOT + method + "(",
                        String.format("cn.hutool.extra.spring.SpringUtil.getBean(tech.yizhichan.sdk.generic.openfeign.DynamicFeignClient.class).%s(\"%s\", \"%s\", ", getDynamicFeignClientMethodByHttpMethod(httpMethod), feignClientName, url));
            } else if (StringUtils.containsAny(line, NamingCase.toPascalCase(feignClientPropertyName), "GenericFeignClientMappers(", "GenericFeignClientMethodMapper", feignClientName, feignClientPropertyName)) {
                line = "\n";
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private String getDynamicFeignClientMethodByHttpMethod(String httpMethod) {
        return String.format("execute%sApi", NamingCase.toPascalCase(httpMethod));
    }

    private Integer getIndexIfContains(String line, List<String> feignApiMethods) {
        for (int i = 0; i < feignApiMethods.size(); i++) {
            if (StringUtils.contains(line, feignApiMethods.get(i))) {
                return i;
            }
        }
        return null;
    }

    private String resolveContent(String text, List<String> leftBounds, String rightBound) {
        for (String leftBound : leftBounds) {
            String target = StringUtils.substringBetween(text, leftBound, rightBound);
            if (StringUtils.isBlank(target)) {
                continue;
            }
            return target.trim();
        }
        return text;
    }

    private List<String> resolveContents(String text, List<String> leftBounds, String rightBound) {
        List<String> targets = new ArrayList<>();
        for (String leftBound : leftBounds) {
            String[] keywords = StringUtils.substringsBetween(text, leftBound, rightBound);
            if (keywords == null || keywords.length == 0) {
                continue;
            }
            targets.addAll(Arrays.asList(keywords));
        }
        return targets;
    }
}
