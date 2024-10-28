package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author auto
 * @description ListHotfixTaskResponse
 */
@Data
public class ListHotfixTaskResponse extends BaseResponse<ListHotfixTaskResponse> {

    private List<HotfixVO> data;


    @Data
    public static class HotfixVO {
        private Long hid;

        /**
         * 租戶命名空间
         */
        private String namespace;

        /**
         * 目标服务名与spring.application.name一致
         */
        private String appname;

        /**
         * 要修复的类路径
         */
        private String classpath;

        /**
         * 要修复的类方法
         */
        private String methodName;

        /**
         * 要修复的方法参数类型集合
         */
        private List<String> argTypes;

        /**
         * 要修复的方法参数名称集合逗号分割
         * 要修复的方法参数集合
         */
        private List<String> argNames;

        /**
         * 修复代码的方法参数值集合逗号分割
         */
        private List<String> argValues;

        /**
         * 返回类型
         */
        private String returnType;

        /**
         * 修复的代码
         */
        private String code;

        /**
         * 功能描述
         */
        private String description;
    }

}