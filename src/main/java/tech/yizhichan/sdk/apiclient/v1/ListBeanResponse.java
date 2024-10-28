package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author auto
 * @description ListBeanResponse
 */
@Data
public class ListBeanResponse extends BaseResponse<ListBeanResponse> {

    private List<BeanVO> data;

    @Data
    public static class BeanVO {
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

}