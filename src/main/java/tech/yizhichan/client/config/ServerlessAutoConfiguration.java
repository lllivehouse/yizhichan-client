package tech.yizhichan.client.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import tech.zhizheng.common.apiclient.annotation.EnableOpenAPiClient;
import tech.zhizheng.common.apiclient.auth.ApiToken;
import tech.zhizheng.common.apiclient.config.OpenApiClientConfiguration;
import tech.zhizheng.common.apiclient.config.OpenApiClientProperties;
import tech.zhizheng.common.apiclient.core.ApiClient;
import tech.zhizheng.common.apiclient.core.DefaultApiClient;
import tech.zhizheng.common.apiclient.core.DefaultClientProfile;
import tech.zhizheng.common.apiclient.core.sse.SseConnectionManager;
import tech.zhizheng.common.model.biz.serverless.BeanCategoryEnum;
import tech.zhizheng.common.model.sse.SseMsgIdEnum;
import tech.yizhichan.client.apiclient.v1.*;
import tech.yizhichan.client.cache.GroovyClassCacheStore;
import tech.yizhichan.client.cache.RestrictionClassCacheStore;
import tech.yizhichan.client.convertor.ObjectMapper;
import tech.yizhichan.client.core.compiler.*;
import tech.yizhichan.client.core.threadpool.BeanQueueThreadPoolExecutorFactory;
import tech.yizhichan.client.core.threadpool.HotfixQueueThreadPoolExecutorFactory;
import tech.yizhichan.client.exception.ServerlessClientException;
import tech.yizhichan.client.generic.openfeign.DynamicFeignClient;
import tech.yizhichan.client.generic.openfeign.DynamicFeignClientFactory;
import tech.yizhichan.client.interceptor.FunctionInjectionInterceptor;
import tech.yizhichan.client.interceptor.WorkflowInjectionInterceptor;
import tech.yizhichan.client.listener.BeanChangeListener;
import tech.yizhichan.client.listener.HotFixChangeListener;
import tech.yizhichan.client.listener.SseDataStreamListener;
import tech.yizhichan.client.queue.*;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.DefaultParameterNameDiscoverer;
import tech.yizhichan.client.v1.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @description: ServerlessAutoConfiguration
 * @author: lex
 * @date: 2024-09-25
 **/
@EnableConfigurationProperties({ServerlessProperties.class})
@ConditionalOnProperty(prefix = "apaas.serverless", name = "enable", havingValue = "true")
@EnableOpenAPiClient
@Slf4j
public class ServerlessAutoConfiguration implements ApplicationContextAware, CommandLineRunner, DisposableBean {
    private ApplicationContext applicationContext;

    @Bean(name = "defaultParameterNameDiscoverer")
    public DefaultParameterNameDiscoverer defaultParameterNameDiscoverer() {
        return new DefaultParameterNameDiscoverer();
    }

    @Bean(name = "functionInjectionInterceptor")
    public FunctionInjectionInterceptor functionInjectionInterceptor(final DefaultParameterNameDiscoverer defaultParameterNameDiscoverer) {
        return new FunctionInjectionInterceptor(defaultParameterNameDiscoverer);
    }

    @Bean(name = "workflowInjectionInterceptor")
    public WorkflowInjectionInterceptor workflowInjectionInterceptor(final DefaultParameterNameDiscoverer defaultParameterNameDiscoverer) {
        return new WorkflowInjectionInterceptor(defaultParameterNameDiscoverer);
    }

    @Bean(name = "beanQueueConsumer")
    public BeanQueueConsumer beanQueueConsumer() {
        return new BeanQueueConsumer();
    }

    @Bean(name = "beanProcessQueue")
    public BeanProcessQueue beanProcessQueue(final BeanQueueConsumer beanQueueConsumer) {
        ExecutorService executorService = BeanQueueThreadPoolExecutorFactory.create();
        return new BeanProcessQueue(beanQueueConsumer, executorService);
    }

    @Bean(name = "hotfixQueueConsumer")
    public HotfixQueueConsumer hotfixQueueConsumer() {
        return new HotfixQueueConsumer();
    }

    @Bean(name = "hotfixProcessQueue")
    public HotfixProcessQueue hotfixProcessQueue(final HotfixQueueConsumer hotfixQueueConsumer) {
        ExecutorService executorService = HotfixQueueThreadPoolExecutorFactory.create();
        return new HotfixProcessQueue(hotfixQueueConsumer, executorService);
    }

    @Bean
    public BeanChangeListener beanChangeListener(final BeanProcessQueue beanProcessQueue) {
        return new BeanChangeListener(beanProcessQueue);
    }

    @Bean
    public HotFixChangeListener hotFixChangeListener(final HotfixProcessQueue hotfixProcessQueue) {
        return new HotFixChangeListener(hotfixProcessQueue);
    }

    @Bean(name = "dynamicPackageScanner")
    public DynamicPackageScanner dynamicPackageScanner() {
        return new DynamicPackageScanner();
    }

    @Bean(name = "dynamicFeignClient")
    public DynamicFeignClient dynamicFeignClient() {
        return new DynamicFeignClient(new DynamicFeignClientFactory<>(applicationContext));
    }

    @Override
    public void run(String... args) {
        try {
            OpenApiClientProperties openApiClientProperties = applicationContext.getBean(OpenApiClientProperties.class);
            ServerlessProperties serverlessProperties = applicationContext.getBean(ServerlessProperties.class);
            // 初始化 ApiClient
            initApiClient(openApiClientProperties, serverlessProperties);
            // 启动 ByteBuddyAgent
            ByteBuddyAgent.install();
            this.connectSSE();
            this.loadRestrictionList();
            this.loadGroovyBean();
            this.loadHotfixTasks();
        } catch (Throwable t) {
            log.error("Failed to init serverless", t);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() {
        SseConnectionManager.disconnect(DataFetchClient.getSseClient());
    }

    private void initApiClient(final OpenApiClientProperties openApiClientProperties, final ServerlessProperties serverlessProperties) throws ServerlessClientException {
        OpenApiClientProperties.AppMetadata appMetadata = openApiClientProperties.getApp().get(OpenApiClientProperties.ApplicationName.SERVERLESS_CENTER.getValue());
        ApiClient defaultClient = new DefaultApiClient(DefaultClientProfile.getProfile(appMetadata.getHost(), appMetadata.getVersion()));
        ApiClientRegisterResponse.AuthenticationResponse res = defaultClient.call(new ApiClientRegisterRequest(serverlessProperties.getNamespace(), serverlessProperties.getAppname(), null)).getData();
        if (StringUtils.isAnyBlank(res.getApiToken(), res.getTokenName())) {
            throw new ServerlessClientException("Failed to register serverless api client");
        }
        ApiToken apiToken = new ApiToken(res.getTokenName(), res.getApiToken());
        ApiClient apiClient = OpenApiClientConfiguration.OpenApiClientConfigUtil.newApiClient(appMetadata, apiToken, false);
        String clientId = serverlessProperties.getNamespace() + "-" + serverlessProperties.getAppname();
        DefaultApiClient sseClient = OpenApiClientConfiguration.OpenApiClientConfigUtil.newDefaultApiClient(appMetadata, apiToken, clientId, true);
        DataFetchClient.setApiClient(apiClient);
        DataFetchClient.setSseClient(sseClient);
    }

    private void connectSSE() {
        Collection<SseDataStreamListener> listeners = applicationContext.getBeansOfType(SseDataStreamListener.class).values();
        // 连接SSE
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                DefaultApiClient sseClient = DataFetchClient.getSseClient();
                SseConnectionManager.connect(sseClient, new SseConnectRequest(), serverSentMessage -> {
                    log.info("从服务端收到数据={}", serverSentMessage);
                    String messageId = serverSentMessage.getMessageId();
                    SseMsgIdEnum sseMsgId = SseMsgIdEnum.getByMsgId(messageId);
                    listeners.forEach(listener -> {
                        if (listener.messageId() == sseMsgId) {
                            listener.onMessage(serverSentMessage.getMessageBody());
                        }
                    });
                });
            }
        }, 60 * 1000, 10 * 1000);
    }

    private void loadRestrictionList() {
        ListRestrictionResponse.RestrictionVO vo = DataFetchClient.listRestriction().getData();
        if (vo == null) {
            return;
        }
        if (CollectionUtil.isNotEmpty(vo.getBlacklist())) {
            RestrictionClassCacheStore.putBlacklist(vo.getBlacklist());
        }
        if (CollectionUtil.isNotEmpty(vo.getWhitelist())) {
            RestrictionClassCacheStore.putWhitelist(vo.getWhitelist());
        }
    }

    private void loadGroovyBean() {
        List<ListBeanResponse.BeanVO> beans = null;
        try {
            beans = DataFetchClient.listBean().getData();
        } catch (Throwable t) {
            log.error("fail to get serverless beans", t);
        }
        if (CollectionUtils.isEmpty(beans)) {
            log.info("ServerlessBeanAutoConfiguration loadGroovyBean beans is empty");
            return;
        }
        List<ClassMethodWithResponse> mockedClasses = parseMockedClasses(beans);
        GroovyClassLoader classLoader = new GroovyClassLoader(this.getClass().getClassLoader(),
                GroovyCompilerConfigurationDefinition.define(GroovyCompilerSetting.builder()
                        .mockedClasses(mockedClasses)
                        .build()));
        for (ListBeanResponse.BeanVO bean : beans) {
            String code = bean.getCode();
            if (StringUtils.isBlank(code) || GroovyClassCacheStore.get(code.getBytes()) != null) {
                continue;
            }
            Class clazz = EnhancedGroovyClassLoader.create(classLoader).parseClass(code);
            String classname = StringUtils.substringBetween(code, "class ", "{").trim();
            EnhancedGroovyClassLoader.registerBean(classname, clazz, (ConfigurableApplicationContext) applicationContext);
            if (Arrays.asList(BeanCategoryEnum.WEBAPI, BeanCategoryEnum.INNER_API).contains(BeanCategoryEnum.getById(bean.getCategoryId()))) {
                EnhancedGroovyClassLoader.registerController(classname, applicationContext);
            }
        }
    }

    private void loadHotfixTasks() {
        List<ListHotfixTaskResponse.HotfixVO> tasks = DataFetchClient.listHotfixTask().getData();
        if (org.springframework.util.CollectionUtils.isEmpty(tasks)) {
            return;
        }
        HotfixProcessQueue queue = applicationContext.getBean(HotfixProcessQueue.class);
        List<HotfixProcessParameter> parameters = ObjectMapper.INSTANCE.toHotfixProcessParameterList(tasks);
        parameters.forEach(queue::put);
    }

    private List<ClassMethodWithResponse> parseMockedClasses(List<ListBeanResponse.BeanVO> beans) {
        return beans.stream().filter(bean -> bean.getApiMocked().intValue() == 1).map(bean ->
                ClassMethodWithResponse.builder()
                        .className(Optional.ofNullable(StrUtil.subBetween(bean.getCode(), "class ", "{")).orElse(StringUtils.EMPTY).trim())
                        .response(bean.getResponseBody())
                        .build()
        ).collect(Collectors.toList());
    }
}
