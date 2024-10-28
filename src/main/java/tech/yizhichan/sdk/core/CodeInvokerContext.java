package tech.yizhichan.sdk.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: CodeInvokerContext
 * @author: lex
 * @date: 2024-08-19
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CodeInvokerContext<V> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7040215788320860445L;

    private List<GroovyMethodArgument> envVars;
    private List<GroovyMethodArgument> args;
    private Class<V> returnClass;
    private TracingContext trace;
}
