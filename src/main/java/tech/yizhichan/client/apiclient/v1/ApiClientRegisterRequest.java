package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.ApiRequest;
import tech.zhizheng.common.apiclient.http.FormatType;
import tech.zhizheng.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author auto
 * @description ApiClientRegisterRequest
 */
@Getter
@ToString
public class ApiClientRegisterRequest extends ApiRequest<ApiClientRegisterResponse> {

    private String namespace;

    private String appname;

    private Date expiredDate;


    public ApiClientRegisterRequest() {
        super("client", "register");
        this.setMethod(MethodType.POST);
        this.setHttpContentType(FormatType.JSON);
    }

    public ApiClientRegisterRequest(String namespace, String appname, Date expiredDate) {
        this();
        this.setNamespace(namespace);
        this.setAppname(appname);
        this.setExpiredDate(expiredDate);

    }

    @Override
    public Class<ApiClientRegisterResponse> getResponseClass() {
        return ApiClientRegisterResponse.class;
    }


    public void setNamespace(String namespace) {
        this.namespace = namespace;
        if (StringUtils.isNotBlank(namespace)) {
            this.putBodyParameter("namespace", namespace);
        }
    }

    public void setAppname(String appname) {
        this.appname = appname;
        if (StringUtils.isNotBlank(appname)) {
            this.putBodyParameter("appname", appname);
        }
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
        if (null != expiredDate) {
            this.putBodyParameter("expiredDate", expiredDate);
        }
    }

}
