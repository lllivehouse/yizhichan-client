package tech.yizhichan.sdk.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: ServerlessProperties
 * @author: lex
 * @date: 2024-08-21
 **/
@Data
@ConfigurationProperties(prefix = "apaas.serverless")
public class ServerlessProperties {

    private String namespace;

    @Value("${spring.application.name}")
    private String appname;

    private Integer retryTimes = Integer.valueOf(3);
}
