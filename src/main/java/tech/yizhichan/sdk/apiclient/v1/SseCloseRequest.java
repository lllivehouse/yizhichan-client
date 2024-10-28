package tech.yizhichan.sdk.apiclient.v1;

import tech.yizhichan.common.apiclient.core.sse.SseRequest;
import tech.yizhichan.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author lex
 * @createTime 2024/8/30
 * @description SseCloseRequest
 */
@Getter
@ToString
public class SseCloseRequest extends SseRequest {

    public SseCloseRequest() {
        super("sse", "close");
        this.setMethod(MethodType.POST);
    }
}
