package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.ApiRequest;
import tech.yizhichan.common.apiclient.http.FormatType;
import tech.yizhichan.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author auto
 * @description ListHotfixTaskRequest
 */
@Getter
@ToString
public class ListHotfixTaskRequest extends ApiRequest<ListHotfixTaskResponse> {

    public ListHotfixTaskRequest() {
        super("hotfix", "list");
        this.setMethod(MethodType.POST);
        this.setHttpContentType(FormatType.JSON);
    }

    @Override
    public Class<ListHotfixTaskResponse> getResponseClass() {
        return ListHotfixTaskResponse.class;
    }
}
