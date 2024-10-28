package tech.yizhichan.sdk.core;

import tech.yizhichan.common.utils.GsonFactory;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: InvokedMethodArgument
 * @author: lex
 * @date: 2024-08-19
 **/
@Data
public class InvokedMethodArgument<V> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1049527836618738931L;

    private Class<V> valueType;
    private V value;

    public InvokedMethodArgument(String val, String argClasspath) throws ClassNotFoundException {
        this.valueType = (Class<V>) Class.forName(argClasspath);
        this.value = GsonFactory.fromJson(val, valueType);
    }
}
