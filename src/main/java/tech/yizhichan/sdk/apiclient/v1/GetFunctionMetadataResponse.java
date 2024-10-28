package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author auto
 * @description GetFunctionMetadataResponse
 */
@Data
public class GetFunctionMetadataResponse extends BaseResponse<GetFunctionMetadataResponse> {

    private FunctionVO data;


    @Data
    public static class FunctionVO {
        private Long fid;

        private String code;

        private List<String> inVarNameList;

        private String functionName;

        private String classpath;

        private String description;

        private List<String> outVarNameList;

        private Integer functionType;

        private Integer version;

        private Integer timeout;

    }

}