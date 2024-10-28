package tech.yizhichan.sdk.core.checker.transform;

/**
 * @description: GroovyCodeTransformer
 * @author: lex
 * @date: 2024-10-02
 **/
public interface GroovyCodeTransformer {

    boolean detect(String code);

    String transform(String code);
}
