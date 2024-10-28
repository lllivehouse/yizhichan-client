package tech.yizhichan.client.core.compiler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: ClassMethodWithResponse
 * @author: lex
 * @date: 2024-09-26
 **/
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class ClassMethodWithResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 5905390674275727530L;

    private String className;

    private String methodName;

    private Object response;
}
