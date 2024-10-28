package tech.yizhichan.client.core.compiler;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.extra.spring.SpringUtil;
import tech.yizhichan.client.cache.RestrictionClassCacheStore;
import tech.yizhichan.client.core.checker.AuthorizedExpressionChecker;
import tech.yizhichan.client.core.checker.ForbiddenClassMethodInterceptor;
import tech.yizhichan.client.core.checker.MockedClassMethodInterceptor;
import tech.yizhichan.client.core.checker.transform.GroovyCodeTransformer;
import com.google.common.collect.Lists;
import groovy.transform.CompileStatic;
import groovy.util.logging.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @description: GroovyCompilerConfigurationDefinition
 * @author: lex
 * @date: 2024-09-26
 **/
public class GroovyCompilerConfigurationDefinition {

    public static CompilerConfiguration define(GroovyCompilerSetting setting) {
        var config = new CompilerConfiguration();
        config.setScriptBaseClass(Optional.ofNullable(setting.getScriptBaseClass()).orElse("groovy.lang.Script"));
        config.setTargetDirectory(Optional.ofNullable(setting.getTargetDirectory()).orElse(ClassLoaderUtil.getClassLoader().getResource("./").getPath()));
        config.setDebug(Optional.ofNullable(setting.getDebug()).orElse(false));
        var customizer = new SecureASTCustomizer();
        // 允许使用package
        customizer.setPackageAllowed(Optional.ofNullable(setting.getPackageAllowed()).orElse(true));
        customizer.addExpressionCheckers(new AuthorizedExpressionChecker(RestrictionClassCacheStore.getBlacklist(), RestrictionClassCacheStore.getWhitelist()));
        config.addCompilationCustomizers(
                customizer,
                new ASTTransformationCustomizer(CompileStatic.class),
                new ASTTransformationCustomizer(Log.class),
                new SandboxTransformer());
        List<ClassMethod> staticMethods = Lists.newArrayList(
                ClassMethod.builder().classpath(System.class.getCanonicalName()).methodName("exit").build(),
                ClassMethod.builder().classpath(Runtime.class.getCanonicalName()).build()
        );
        staticMethods.addAll(Optional.ofNullable(setting.getForbiddenStaticMethods()).orElse(List.of()));
        new ForbiddenClassMethodInterceptor(
                staticMethods,
                setting.getForbiddenInstanceMethods())
                .register();
        if (CollectionUtils.isNotEmpty(setting.getMockedClasses())) {
            new MockedClassMethodInterceptor(setting.getMockedClasses()).register();
        }
        return config;
    }

    public static String preprocessCode(String code) {
        Collection<GroovyCodeTransformer> transformers = SpringUtil.getBeansOfType(GroovyCodeTransformer.class).values();
        for (GroovyCodeTransformer transformer : transformers) {
            try {
                if (transformer.detect(code)) {
                    code = transformer.transform(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return code;
    }
}
