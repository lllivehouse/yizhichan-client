package tech.yizhichan.sdk.exception;

import tech.yizhichan.common.model.exception.IErrorCode;

/**
 * @description: ServerlessClientException
 * @author: lex
 * @date: 2024-08-19
 **/
public class ServerlessClientException extends Exception {
    private IErrorCode code;

    public ServerlessClientException() {
        super();
    }

    public ServerlessClientException(String message) {
        super(message);
    }

    public ServerlessClientException(IErrorCode code) {
        super(code.getCode() + "-" + code.getMsg());
        this.code = code;
    }

    public ServerlessClientException(IErrorCode code, Throwable cause) {
        super(code.getCode() + "-" + code.getMsg(), cause);
        this.code = code;
    }

    public ServerlessClientException(IErrorCode code, String message) {
        super(message, new Exception(code.getCode() + "-" + code.getMsg()));
        this.code = code;
    }

    public ServerlessClientException(IErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public IErrorCode getCode() {
        return code;
    }

    public void setCode(IErrorCode code) {
        this.code = code;
    }
}
