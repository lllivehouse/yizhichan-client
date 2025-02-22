package tech.yizhichan.client.apiclient.v1;

import tech.yizhichan.zhizheng.apiclient.core.BaseResponse;
import lombok.Data;

/**
 * @author auto
 * @description ApiClientRegisterResponse
 */
@Data
public class ApiClientRegisterResponse extends BaseResponse<ApiClientRegisterResponse> {

    private AuthenticationResponse data;


    @Data
    public static class AuthenticationResponse {

        private String tokenName;

        private String apiToken;
    }
}