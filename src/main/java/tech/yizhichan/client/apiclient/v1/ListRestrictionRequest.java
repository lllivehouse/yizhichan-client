package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.ApiRequest;
import tech.zhizheng.common.apiclient.http.FormatType;
import tech.zhizheng.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author auto
 * @description ListRestrictionRequest
 */
@Getter
@ToString
public class ListRestrictionRequest extends ApiRequest<ListRestrictionResponse> {


    public ListRestrictionRequest() {
        super("restriction", "list");
        this.setMethod(MethodType.POST);
        this.setHttpContentType(FormatType.JSON);
    }

    @Override
    public Class<ListRestrictionResponse> getResponseClass() {
        return ListRestrictionResponse.class;
    }


}