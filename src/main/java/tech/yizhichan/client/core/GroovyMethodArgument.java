package tech.yizhichan.client.core;

import tech.zhizheng.common.utils.GsonFactory;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: MethodArgument
 * @author: lex
 * @date: 2024-08-19
 **/
@Data
public class GroovyMethodArgument<V> implements Serializable {

    @Serial
    private static final long serialVersionUID = -5358635956916034737L;

    private Class<V> valueType;
    private String name;
    private V value;

    public GroovyMethodArgument(String name, String val, Class<V> klass) {
        this.valueType = klass;
        this.name = name;
        this.value = GsonFactory.fromJson(val, valueType);
    }

    public GroovyMethodArgument(String name, Object val) {
        this.valueType = (Class<V>) val.getClass();
        this.name = name;
        this.value = (V) val;
    }
}
