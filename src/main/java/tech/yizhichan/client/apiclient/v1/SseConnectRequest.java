package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.sse.SseRequest;
import tech.zhizheng.common.apiclient.http.FormatType;
import tech.zhizheng.common.apiclient.http.MethodType;
import lombok.Getter;
import lombok.ToString;

/**
 * @author lex
 * @createTime 2024/8/30
 * @description SseConnectRequest
 */
@Getter
@ToString
public class SseConnectRequest extends SseRequest {

    public SseConnectRequest() {
        super("sse", "connect");
        this.setMethod(MethodType.GET);
        this.setHttpContentType(FormatType.TEXT_EVENT_STREAM_VALUE);
    }
}
