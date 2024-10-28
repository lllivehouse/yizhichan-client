package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.ApiRequest;
import tech.zhizheng.common.apiclient.http.FormatType;
import tech.zhizheng.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author auto
 * @description GetFunctionMetadataRequest
 */
@Getter
@ToString
public class GetFunctionMetadataRequest extends ApiRequest<GetFunctionMetadataResponse> {

    private String functionName;


    public GetFunctionMetadataRequest() {
        super("function", "metadata");
        this.setMethod(MethodType.POST);
        this.setHttpContentType(FormatType.JSON);
    }

    public GetFunctionMetadataRequest(String functionName) {
        this();
        this.setFunctionName(functionName);

    }

    @Override
    public Class<GetFunctionMetadataResponse> getResponseClass() {
        return GetFunctionMetadataResponse.class;
    }


    public void setFunctionName(String functionName) {
        this.functionName = functionName;
        if (StringUtils.isNotBlank(functionName)) {
            this.putBodyParameter("functionName", functionName);
        }
    }

}
