package tech.yizhichan.client.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: TracingContext
 * @author: lex
 * @date: 2024-08-19
 **/
@SuperBuilder
@Data
@NoArgsConstructor
public class TracingContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 4573749162455464253L;

    private String traceId;
    private String spanId;
    private String parentSpanId;
    private Long duration;
    private Long startTime;
    private Long endTime;
    private String namespace;
    private String appname;
    private String clientIp;
    private String remark;
    private String errMsg;
}
