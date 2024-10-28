package tech.yizhichan.client.core;

/**
 * @description: Connector
 * @author: lex
 * @date: 2024-08-17
 **/
public abstract class Connector extends AbstractComponent {

    public Connector(ConnectorExecutionEngine engine) {
        super(engine);
    }
}
