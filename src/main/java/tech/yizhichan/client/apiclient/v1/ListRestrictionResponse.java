package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author auto
 * @description ListRestrictionResponse
 */
@Data
public class ListRestrictionResponse extends BaseResponse<ListRestrictionResponse> {

    private RestrictionVO data;


    @Data
    public static class RestrictionVO {

        private List<RestrictionClassVO> blacklist;

        private List<RestrictionClassVO> whitelist;

    }

    @Data
    public static class RestrictionClassVO {
        private String className;
        private String methodName;
    }

}