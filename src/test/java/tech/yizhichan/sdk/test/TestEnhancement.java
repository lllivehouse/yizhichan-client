package tech.yizhichan.sdk.test;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.alibaba.bytekit.asm.interceptor.annotation.ExceptionHandler;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AgentUtils;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * @description: TestEnhancement
 * @author: lex
 * @date: 2024-08-15
 **/
public class TestEnhancement {
    private static byte[] oldBytes;

    public static void main(String[] args) throws Exception {
        AgentUtils.install();
        DefaultInterceptorClassParser interceptorClassParser = new DefaultInterceptorClassParser();
        List<InterceptorProcessor> processors = interceptorClassParser.parse(SampleInterceptor.class);

        // 加载字节码
        ClassNode classNode = AsmUtils.loadClass(TestEnhancement.class);
        if (oldBytes == null) {
            oldBytes = AsmUtils.toBytes(classNode);
        }

        // 对加载到的字节码做增强处理
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("test")) {
                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                for (InterceptorProcessor interceptor : processors) {
                    interceptor.process(methodProcessor);
                }
                break;
            }
        }

        // 获取增强后的字节码
        byte[] bytes = AsmUtils.toBytes(classNode);

//        // 查看反编译结果
//        System.out.println(Decompiler.decompile(bytes));

//        // 等待，查看未增强里的输出结果
//        TimeUnit.SECONDS.sleep(10);

        // 通过 reTransform 增强类
        AgentUtils.reTransform(TestEnhancement.class, args != null && args.length > 1 && "stop".equals(args[1]) ? oldBytes : bytes);
    }

    private static class PrintExceptionSuppressHandler {

        @ExceptionHandler(inline = true)
        public static void onSuppress(@Binding.Throwable Throwable e, @Binding.Class Object clazz) {
            System.out.println("exception handler: " + clazz);
            e.printStackTrace();
        }
    }

    private static class SampleInterceptor {

        @AtEnter(inline = true, suppress = RuntimeException.class, suppressHandler = PrintExceptionSuppressHandler.class)
        public static void atEnter(@Binding.This Object object,
                                   @Binding.Class Object clazz,
                                   @Binding.Args Object[] args,
                                   @Binding.MethodName String methodName,
                                   @Binding.MethodDesc String methodDesc) {
            System.out.println("atEnter, args: " + args);
        }

        @AtExit(inline = true)
        public static void atExit(@Binding.Return Object returnObject) {
            System.out.println("atExit, returnObject: " + returnObject);
        }

//        @AtExceptionExit(inline = true, onException = RuntimeException.class)
//        public static void atExceptionExit(@Binding.Throwable RuntimeException ex,
//                                           @Binding.Field(name = "exceptionCount") int exceptionCount) {
//            System.out.println("atExceptionExit, ex: " + ex.getMessage() + ", field exceptionCount: " + exceptionCount);
//        }
    }
}
