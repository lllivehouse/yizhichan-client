package tech.yizhichan.sdk.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lex
 * @createTime 2024/8/31
 * @description HotfixProcessParameter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotfixProcessParameter implements Serializable {
    @Serial
    private static final long serialVersionUID = -3675526503568658865L;
    private String namespace;
    private String appname;
    private String code;
    private String classpath;
    private String methodName;
    private String returnType;
    private List<String> argNames;
    private List<String> argValues;
    private List<String> argTypes;

}