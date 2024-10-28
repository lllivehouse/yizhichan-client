package tech.yizhichan.sdk.core.compiler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: ClassMethod
 * @author: lex
 * @date: 2024-09-26
 **/
@Data
@SuperBuilder
@NoArgsConstructor
public class ClassMethod implements Serializable {

    @Serial
    private static final long serialVersionUID = -7486463874122881009L;

    private String classpath;

    private String methodName;
}
