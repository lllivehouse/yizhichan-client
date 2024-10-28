package tech.yizhichan.sdk.convertor;

import tech.yizhichan.sdk.apiclient.v1.GetFunctionMetadataResponse;
import tech.yizhichan.sdk.apiclient.v1.ListHotfixTaskResponse;
import tech.yizhichan.sdk.core.CodeInvokerContext;
import tech.yizhichan.sdk.core.FunctionExecutionEngine;
import tech.yizhichan.sdk.core.GroovyInvokerConfiguration;
import tech.yizhichan.sdk.core.TracingContext;
import tech.yizhichan.sdk.queue.HotfixProcessParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * @description: ObjectMapper
 * @author: lex
 * @date: 2024-08-22
 **/
@Mapper
public interface ObjectMapper {

    ObjectMapper INSTANCE = Mappers.getMapper(ObjectMapper.class);

    @Mappings({
            @Mapping(target = "threadPoolExecutor", expression = "java(org.apache.commons.lang3.BooleanUtils.isTrue(material.getIsAsync())?tech.yizhichan.sdk.threadpool.core.FunctionThreadPoolExecutorFactory.create():null)"),
    })
    GroovyInvokerConfiguration toGroovyInvokerConfiguration(FunctionExecutionEngine.Material material);

    @Mappings({
            @Mapping(target = "envVars", expression = "java(((java.util.Set<java.util.Map.Entry<java.lang.String,java.lang.Object>>)java.util.Optional.ofNullable(material.getEnvironmentVariables()).orElse(new java.util.HashMap<>()).entrySet()).stream().map(e -> new tech.yizhichan.sdk.core.GroovyMethodArgument(e.getKey(), String.valueOf(e.getValue()), e.getValue().getClass())).collect(java.util.stream.Collectors.toList()))"),
            @Mapping(target = "args", expression = "java(((java.util.Set<java.util.Map.Entry<java.lang.String,java.lang.Object>>)java.util.Optional.ofNullable(material.getArgs()).orElse(new java.util.HashMap<>()).entrySet()).stream().map(e -> new tech.yizhichan.sdk.core.GroovyMethodArgument(e.getKey(), e.getValue())).collect(java.util.stream.Collectors.toList()))"),
    })
    CodeInvokerContext toCodeInvokerContext(FunctionExecutionEngine.Material material);

    @Mappings({
            @Mapping(target = "functionType", expression = "java(tech.yizhichan.common.model.biz.serverless.FunctionTypeEnum.getById(function.getFunctionType()))"),
            @Mapping(target = "code", expression = "java(function.getCode())"),
            @Mapping(target = "codeVersion", expression = "java(java.lang.String.valueOf(function.getVersion()))"),
            @Mapping(target = "timeoutMillis", expression = "java(function.getTimeout())"),
            @Mapping(target = "returnClass", source = "returnClass"),
            @Mapping(target = "exceptionRetryTimes", source = "retryTimes"),
            @Mapping(target = "environmentVariables", source = "envarMap"),
            @Mapping(target = "args", source = "argMap"),
            @Mapping(target = "isAsync", source = "isAsync"),
            @Mapping(target = "trace", source = "tracingContext"),
    })
    FunctionExecutionEngine.Material toFunctionExecutionEngineMaterial(GetFunctionMetadataResponse.FunctionVO function,
                                                                       Class returnClass,
                                                                       Integer retryTimes,
                                                                       Map<String, Object> argMap,
                                                                       Boolean isAsync,
                                                                       Map<String, Object> envarMap,
                                                                       TracingContext tracingContext);

    List<HotfixProcessParameter> toHotfixProcessParameterList(List<ListHotfixTaskResponse.HotfixVO> vos);
}
