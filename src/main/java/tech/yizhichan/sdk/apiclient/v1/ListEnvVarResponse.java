package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author auto
 * @description ListEnvVarResponse
 */
@Data
public class ListEnvVarResponse extends BaseResponse<ListEnvVarResponse> {

    private List<EnvVarVO> data;


    @Data
    public static class EnvVarVO {
        /**
         * 目标服务名与spring.application.name一致
         */
        private String appname;

        /**
         * 环境变量名
         */
        private String varName;

        /**
         * 环境变量类型
         */
        private String varClasspath;

        /**
         * 环境变量值
         */
        private String varValue;

        /**
         * 变量描述
         */
        private String description;
    }

}
