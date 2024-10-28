package tech.yizhichan.client.apiclient.v1;

import tech.zhizheng.common.apiclient.core.ApiClient;
import tech.zhizheng.common.apiclient.core.DefaultApiClient;
import tech.zhizheng.common.utils.GsonFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: DataFetchClient
 * @author: lex
 * @date: 2024-08-22
 **/
@Slf4j
public final class DataFetchClient {

    private static ApiClient apiClient;
    private static DefaultApiClient sseClient;

    public static GetFunctionMetadataResponse getAppliedFunctionByName(String functionName) {
        Preconditions.checkArgument(apiClient != null);
        GetFunctionMetadataResponse response = apiClient.call(new GetFunctionMetadataRequest(functionName));
        return response;
    }

    public static ListEnvVarResponse listEnvVar() {
        Preconditions.checkArgument(apiClient != null);
        ListEnvVarResponse response = apiClient.call(new ListEnvVarRequest());
        return response;
    }

    public static ListBeanResponse listBean() {
        Preconditions.checkArgument(apiClient != null);
        ListBeanResponse response = apiClient.call(new ListBeanRequest());
        return response;
    }

    public static Map<String, Object> getEnvarMap() {
        List<ListEnvVarResponse.EnvVarVO> envVars = listEnvVar().getData();
        if (CollectionUtils.isEmpty(envVars)) {
            return Maps.newHashMap();
        }
        return envVars.stream().collect(Collectors.toMap(e -> e.getVarName(), e -> {
            String varValue = e.getVarValue();
            try {
                return GsonFactory.fromJson(varValue, Class.forName(e.getVarClasspath()));
            } catch (ClassNotFoundException ex) {
                log.error("toEnvarMap error for ClassNotFound:{}", e.getVarClasspath(), ex);
                return null;
            }
        }));
    }

    public static ListHotfixTaskResponse listHotfixTask() {
        Preconditions.checkArgument(apiClient != null);
        ListHotfixTaskResponse response = apiClient.call(new ListHotfixTaskRequest());
        return response;
    }

    public static ListRestrictionResponse listRestriction() {
        Preconditions.checkArgument(apiClient != null);
        ListRestrictionResponse response = apiClient.call(new ListRestrictionRequest());
        return response;
    }

    public static void setApiClient(ApiClient apiClient) {
        DataFetchClient.apiClient = apiClient;
    }

    public static void setSseClient(DefaultApiClient sseClient) {
        DataFetchClient.sseClient = sseClient;
    }

    public static DefaultApiClient getSseClient() {
        if (sseClient instanceof DefaultApiClient) {
            return sseClient;
        }
        throw new IllegalArgumentException("SseClient is not DefaultApiClient");
    }
}
