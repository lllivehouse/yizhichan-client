package tech.yizhichan.sdk.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author lex
 * @createTime 2024/9/25
 * @description BeanProcessParameter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeanProcessParameter implements Serializable {
    @Serial
    private static final long serialVersionUID = 4974730196273951003L;
    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 唯一类名
     */
    private String beanName;

    /**
     * 类型id, 0:WebApi 1:Component 2:POGO 3:InnerApi 4:Helper 5:Configuration 6:Interface 7:Enum 8:Annotation
     */
    private Integer categoryId;

    /**
     * 代码
     */
    private String code;

    /**
     * 超时时间单位毫秒
     */
    private Integer timeout;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 运行模式:0共享 1托管
     */
    private Byte runMode;

    /**
     * 共享模式下目标服务名
     */
    private String appname;

    /**
     * api请求方法:get,post,put,delete
     */
    private String requestMethod;

    /**
     * api路由地址
     */
    private String apiUrl;

    /**
     * api请求体
     */
    private String requestBody;

    /**
     * api是否mock
     */
    private Byte apiMocked;

    /**
     * api返回体 mocked=1时为mock返回
     */
    private String responseBody;

}