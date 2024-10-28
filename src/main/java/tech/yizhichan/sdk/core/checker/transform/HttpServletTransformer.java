package tech.yizhichan.sdk.core.checker.transform;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Component;

/**
 * @description: HttpServletTransformer
 * @author: lex
 * @date: 2024-10-02
 **/
@Component
public class HttpServletTransformer implements GroovyCodeTransformer {

    @Override
    public boolean detect(String code) {
        return StringUtils.containsAny(code, "HttpServletRequest", "HttpServletResponse", ".servlet.http.HttpServletRequest", ".servlet.http.HttpServletResponse");
    }

    @Override
    public String transform(String code) {
        String springBootVersion = StringUtils.substringBefore(SpringBootVersion.getVersion(), StrUtil.DOT);
        if ("3".equals(springBootVersion)) {
            return code.replaceAll("javax.servlet.http.", "jakarta.servlet.http.");
        } else if ("2".equals(springBootVersion)) {
            return code.replaceAll("jakarta.servlet.http.", "javax.servlet.http.");
        }
        return code;
    }
}
