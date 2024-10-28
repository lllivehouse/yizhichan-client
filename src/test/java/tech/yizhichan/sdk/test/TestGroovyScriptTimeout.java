package tech.yizhichan.sdk.test;

import tech.yizhichan.common.utils.cache.CacheStore;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.util.logging.Slf4j;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.springframework.util.DigestUtils;

import java.io.IOException;

/**
 * @description: TestGroovyScriptTimeout
 * @author: lex
 * @date: 2024-08-15
 **/
@Slf4j
public class TestGroovyScriptTimeout {
    private final CacheStore<String, GroovyObject> groovyObjectCache = new CacheStore.Builder().setCapacity(10000).setExpireSec(3600 * 24 * 7).build();

    public static void main(String[] args) {
        String code = "import groovy.transform.TimedInterrupt\n" +
                "\n" +
                "import java.util.concurrent.TimeUnit\n" +
                "\n" +
                "class GroovyScriptTest {\n" +
                "\n" +
//                "    @TimedInterrupt(unit = TimeUnit.MILLISECONDS, value = 10000)\n" +
                "    @TimedInterrupt(value = 10L)\n" +
                "    @groovy.transform.ThreadInterrupt\n" +
                "    public String run(String key) {\n" +
                "        while (true) {\n" +
                "            print(11);\n" +
                "        }\n" +
                "        return key + \":updated\";\n" +
                "    }\n" +
                "}\n";
        GroovyObject groovyObject = new TestGroovyScriptTimeout().buildScript(code);
        Object[] vars = {""};
        String text = (String) groovyObject.invokeMethod("run", vars);
        System.out.println("执行返回:" + text);
    }

    /**
     * 加载脚本
     *
     * @param script
     * @return
     */
    @SuppressWarnings("removal")
    public GroovyObject buildScript(String script) {
        if (StringUtils.isEmpty(script)) {
            throw new RuntimeException("script is empty");
        }

        String cacheKey = DigestUtils.md5DigestAsHex(script.getBytes());
        if (groovyObjectCache.get(cacheKey) != null) {
            return groovyObjectCache.get(cacheKey);
        }

        var config = new CompilerConfiguration();
        config.setScriptBaseClass("groovy.lang.Script");
        config.setTargetDirectory(this.getClass().getClassLoader().getResource("./").getPath());
        var customizer = new SecureASTCustomizer();
        customizer.setPackageAllowed(true); // 允许使用package
        customizer.addExpressionCheckers(new NoSupportClassTest());
        config.addCompilationCustomizers(
                customizer,
//                new ASTTransformationCustomizer(ThreadInterrupt.class),
//                new ASTTransformationCustomizer(ImmutableMap.of("value", 10), TimedInterrupt.class),
                new SandboxTransformer());
        new NoSystemExitSandbox().register();
        new NoRunTimeSandbox().register();
        GroovyClassLoader classLoader = new GroovyClassLoader(this.getClass().getClassLoader(), config);
        try {
            Class<?> groovyClass = classLoader.parseClass(script);
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            classLoader.clearCache();

            groovyObjectCache.put(cacheKey, groovyObject);
            System.out.println("groovy buildScript success" + groovyObject);
            return groovyObject;
        } catch (Throwable e) {
            throw new RuntimeException("buildScript error", e);
        } finally {
            try {
                classLoader.close();
            } catch (IOException e) {
                System.err.println("close GroovyClassLoader error" + e.getMessage());
            }
        }
    }

    public static class NoSupportClassTest implements SecureASTCustomizer.ExpressionChecker {
        @Override
        public boolean isAuthorized(Expression expression) {
            System.out.println(expression);
            if (expression instanceof MethodCallExpression) {
                MethodCallExpression mc = (MethodCallExpression) expression;
                String className = mc.getReceiver().getText();
                String method = mc.getMethodAsString();

                System.out.println("=====>" + className + "." + method);

            }
            return true;
        }
    }

    class NoSystemExitSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == System.class && method == "exit") {
                throw new SecurityException("No call on System.exit() please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }

    class NoRunTimeSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == Runtime.class) {
                throw new SecurityException("No call on RunTime please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }
}
