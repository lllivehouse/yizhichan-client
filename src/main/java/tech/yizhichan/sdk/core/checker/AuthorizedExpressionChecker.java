package tech.yizhichan.sdk.core.checker;

import tech.yizhichan.sdk.apiclient.v1.ListRestrictionResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.util.List;

/**
 * @description: AuthorizedExpressionChecker
 * @author: lex
 * @date: 2024-09-26
 **/
@RequiredArgsConstructor
public class AuthorizedExpressionChecker implements SecureASTCustomizer.ExpressionChecker {

    private final List<ListRestrictionResponse.RestrictionClassVO> blacklist;
    private final List<ListRestrictionResponse.RestrictionClassVO> whitelist;

    @Override
    public boolean isAuthorized(Expression expression) {
        if (expression instanceof MethodCallExpression) {
            MethodCallExpression mc = (MethodCallExpression) expression;
            String className = mc.getReceiver().getText();
            String method = mc.getMethodAsString();
            if (CollectionUtils.isNotEmpty(blacklist)) {
                return !blacklist.stream().anyMatch(black -> StringUtils.equals(black.getClassName(), className) && StringUtils.equals(black.getMethodName(), method));
            }
            if (CollectionUtils.isNotEmpty(whitelist)) {
                return whitelist.stream().anyMatch(white -> StringUtils.equals(white.getClassName(), className) && StringUtils.equals(white.getMethodName(), method));
            }
        }
        return true;
    }
}