package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.ApiRequest;
import tech.yizhichan.common.apiclient.http.FormatType;
import tech.yizhichan.common.apiclient.http.MethodType;
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