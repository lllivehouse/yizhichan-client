package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.ApiRequest;
import tech.zhizheng.common.apiclient.http.FormatType;
import tech.zhizheng.common.apiclient.http.MethodType;
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
