package tech.yizhichan.sdk.core.compiler;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @description: GroovyCompilerSetting
 * @author: lex
 * @date: 2024-09-26
 **/
@Data
@SuperBuilder
@NoArgsConstructor
public class GroovyCompilerSetting {
    private String scriptBaseClass;
    private String targetDirectory;
    private Boolean packageAllowed;
    private Boolean debug;
    private Integer timeoutSecond;
    private List<ClassMethod> forbiddenStaticMethods;
    private List<ClassMethod> forbiddenInstanceMethods;
    private List<ClassMethodWithResponse> mockedClasses;
}
