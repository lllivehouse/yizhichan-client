package tech.yizhichan.client.exception;

import tech.zhizheng.common.model.exception.IErrorCode;

/**
 * @description: ClientErrorCodeEnum
 * @author: lex
 * @date: 2024-08-19
 **/
public enum ClientErrorCodeEnum implements IErrorCode {

    ILLEGAL_ARGS(4000, "非法请求参数"),
    PRECONDITION_NOT_MATCHED(4300, "方法前置条件不符合"),
    GROOVY_BUILD_ERROR(4400, "groovy脚本编译错误"),
    GROOVY_RUNTIME_ERROR(4500, "groovy脚本运行时异常"),
    GROOVY_RUNTIME_PARSER_ERROR(4501, "groovy脚本运行时解析错误"),
    GROOVY_INVOKER_TIMEOUT_ERROR(4502, "groovy脚本执行超时错误"),
    GROOVY_MISSING_CLASS_ERROR(4503, "groovy脚本缺少类错误"),
    GROOVY_MISSING_METHOD_ERROR(4504, "groovy脚本缺少方法错误"),
    GROOVY_ILLEGAL_PROPERTY_ERROR(4505, "groovy脚本非法属性错误"),
    GROOVY_MISSING_FIELD_ERROR(4506, "groovy脚本缺少匿名类字段错误"),
    GROOVY_INCORRECT_CLOSURE_ARGS_ERROR(4507, "groovy脚本非法闭包属性错误"),
    ;

    private int code;
    private String message;

    ClientErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }

    public static ClientErrorCodeEnum getByCode(int code) {
        for (ClientErrorCodeEnum errorCodeEnum : ClientErrorCodeEnum.values()) {
            if (errorCodeEnum.getCode() == code) {
                return errorCodeEnum;
            }
        }
        return null;
    }
}
