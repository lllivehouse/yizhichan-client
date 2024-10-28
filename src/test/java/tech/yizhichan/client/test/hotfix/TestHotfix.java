package tech.yizhichan.client.test.hotfix;

import cn.hutool.core.util.ClassLoaderUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

/**
 * @description: TestHotfix
 * @author: lex
 * @date: 2024-08-16
 **/
@RestController
@RequiredArgsConstructor
@Slf4j
public class TestHotfix {
    private static Instrumentation instrumentation;

    private final TestService testService;

    private static final Cache<String, GroovyObject> groovyObjectCache = CacheBuilder.newBuilder().maximumSize(Integer.MAX_VALUE).weakValues().expireAfterAccess(7 * 24 * 3600, TimeUnit.SECONDS).expireAfterWrite(7 * 24 * 3600, TimeUnit.SECONDS).build();

    static {
        instrumentation = getInstrumentation();
    }

    @GetMapping("/hotfix")
    public void hotfix() throws IllegalAccessException, ClassNotFoundException {
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class) // 可以选择任何基类，这里我们使用 Object
                .name(StringUtils.substringBeforeLast("com.slid.live.slot.api.activity.modules.marketing.service.TestService", ".") + ".GeneratedInterceptor") // 指定生成的类名
                .defineMethod("printForGroovyTest", Class.forName("org.springframework.http.ResponseEntity"), Visibility.PUBLIC, Ownership.STATIC)
                .withParameters(Class.forName("java.lang.String"))
                .intercept(FixedValue.value(buildScript("import groovy.transform.TimedInterrupt\n" +
                        "\n" +
                        "import java.util.concurrent.TimeUnit\n" +
                        "\n" +
                        "class GroovyScriptTest {\n" +
                        "\n" +
//                "    @TimedInterrupt(unit = TimeUnit.MILLISECONDS, value = 10000)\n" +
                        "    @TimedInterrupt(value = 10L)\n" +
                        "    @groovy.transform.ThreadInterrupt\n" +
                        "    public org.springframework.http.ResponseEntity<String> run() {\n" +
                        "        println('holy shit');\n" +
                        "        return org.springframework.http.ResponseEntity.ok('oh my god');\n" +
                        "    }\n" +
                        "}\n").invokeMethod("run", null)))
                .make()
                .load(ClassLoaderUtil.getClassLoader(), ClassLoadingStrategy.UsingLookup.of(MethodHandles.privateLookupIn(Class.forName("com.slid.live.slot.api.activity.modules.marketing.service.TestService"), MethodHandles.lookup())))
                .getLoaded();

        new ByteBuddy()
                .redefine(Class.forName("com.slid.live.slot.api.activity.modules.marketing.service.TestService"))
                .method(ElementMatchers.named("printForGroovyTest").and(ElementMatchers.returns(Class.forName("org.springframework.http.ResponseEntity"))))
                .intercept(MethodDelegation.to(dynamicType))
                .make()
                .load(ClassLoaderUtil.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent(ClassReloadingStrategy.Strategy.RETRANSFORMATION));
    }

    @GetMapping("/print")
    public ResponseEntity<String> print() {
        return testService.print("hello world");
    }

    private static Instrumentation getInstrumentation() {
        if (null == instrumentation) {
            instrumentation = ByteBuddyAgent.install();
        }
        return instrumentation;
    }

    public static GroovyObject buildScript(String script) {
        if (StringUtils.isEmpty(script)) {
            throw new RuntimeException("script is empty");
        }

        String cacheKey = DigestUtils.md5DigestAsHex(script.getBytes());
        if (groovyObjectCache.getIfPresent(cacheKey) != null) {
            log.debug("groovyObjectCache hit");
            return groovyObjectCache.getIfPresent(cacheKey);
        }

        var config = new CompilerConfiguration();
        config.setScriptBaseClass("groovy.lang.Script");
        config.setTargetDirectory(ClassLoaderUtil.getClassLoader().getResource("./").getPath());
        var customizer = new SecureASTCustomizer();
        customizer.setPackageAllowed(true); // 允许使用package
        customizer.addExpressionCheckers(new TestHotfix.NoSupportClassTest());
        config.addCompilationCustomizers(
                customizer,
//                new ASTTransformationCustomizer(ThreadInterrupt.class),
//                new ASTTransformationCustomizer(ImmutableMap.of("value", 10), TimedInterrupt.class),
                new SandboxTransformer());
        new TestHotfix.NoSystemExitSandbox().register();
        new TestHotfix.NoRunTimeSandbox().register();
        GroovyClassLoader classLoader = new GroovyClassLoader(GroovyShell.class.getClassLoader(), config);
        try {
            Class<?> groovyClass = classLoader.parseClass(script);
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            classLoader.clearCache();

            groovyObjectCache.put(cacheKey, groovyObject);
            log.info("groovy buildScript success: {}", groovyObject);
            return groovyObject;
        } catch (Throwable e) {
            throw new RuntimeException("buildScript error", e);
        } finally {
            try {
                classLoader.close();
            } catch (IOException e) {
                log.error("close GroovyClassLoader error", e);
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

    static class NoSystemExitSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == System.class && method == "exit") {
                throw new SecurityException("No call on System.exit() please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }

    static class NoRunTimeSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == Runtime.class) {
                throw new SecurityException("No call on RunTime please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }
}
