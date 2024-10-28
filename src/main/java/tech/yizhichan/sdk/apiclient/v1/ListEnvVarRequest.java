package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.ApiRequest;
import tech.yizhichan.common.apiclient.http.FormatType;
import tech.yizhichan.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author auto
 * @description ListEnvVarRequest
 */
@Getter
@ToString
public class ListEnvVarRequest extends ApiRequest<ListEnvVarResponse> {

    public ListEnvVarRequest() {
        super("envar", "list");
        this.setMethod(MethodType.POST);
        this.setHttpContentType(FormatType.JSON);
    }

    @Override
    public Class<ListEnvVarResponse> getResponseClass() {
        return ListEnvVarResponse.class;
    }
}
