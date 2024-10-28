package tech.yizhichan.client.core;

import tech.zhizheng.common.model.R;

/**
 * @description: Workflow
 * @author: lex
 * @date: 2024-08-17
 **/
public final class Workflow extends AbstractComponent {

    public Workflow(WorkflowExecutionEngine engine) {
        super(engine);
    }

    @Override
    public R start() {
        return null;
    }
}
