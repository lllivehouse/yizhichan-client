package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.ApiRequest;
import tech.zhizheng.common.apiclient.http.FormatType;
import tech.zhizheng.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author auto
 * @description ListBeanRequest
 */
@Getter
@ToString
public class ListBeanRequest extends ApiRequest<ListBeanResponse> {

    public ListBeanRequest() {
        super("bean", "list");
        this.setMethod(MethodType.POST);
        this.setHttpContentType(FormatType.JSON);
    }

    @Override
    public Class<ListBeanResponse> getResponseClass() {
        return ListBeanResponse.class;
    }
}