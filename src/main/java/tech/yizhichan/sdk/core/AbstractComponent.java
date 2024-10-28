package tech.yizhichan.sdk.core;

import tech.yizhichan.common.model.R;

/**
 * @description: AbstractComponent
 * @author: lex
 * @date: 2024-08-17
 **/
public abstract class AbstractComponent {

    private ExecutionEngine engine;

    public AbstractComponent(ExecutionEngine engine) {
        this.engine = engine;
    }

    public R start() {
        return engine.exec();
    }
}
