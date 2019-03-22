package com.github.rodbate.datax.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.protocol.request.DataXJobReportRequest;
import com.github.rodbate.datax.common.report.DataXJobReportInfo;
import com.github.rodbate.datax.common.service.IService;
import com.github.rodbate.datax.common.util.RetryUtil;
import com.github.rodbate.datax.server.client.AgentClientManager;
import com.github.rodbate.datax.server.client.ClientHouseKeepingManager;
import com.github.rodbate.datax.server.client.ClientType;
import com.github.rodbate.datax.server.client.CoreJobClientManager;
import com.github.rodbate.datax.server.client.loadbalance.LoadBalance;
import com.github.rodbate.datax.server.client.loadbalance.RoundRobinAgentLoadBalance;
import com.github.rodbate.datax.server.config.DataXProperties;
import com.github.rodbate.datax.server.okhttp.OkHttpClientUtils;
import com.github.rodbate.datax.server.processors.ClientManagerProcessor;
import com.github.rodbate.datax.server.processors.CoreJobProcessor;
import com.github.rodbate.datax.transport.exceptions.TransportSendException;
import com.github.rodbate.datax.transport.netty.NettyTransportServer;
import com.github.rodbate.datax.transport.netty.config.NettyServerConfig;
import com.github.rodbate.datax.transport.protocol.Packet;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 11:44
 */
@Slf4j
public class DataXServerManager implements IService {

    private final NettyTransportServer transportServer;
    private final AgentClientManager agentClientManager;
    private final CoreJobClientManager coreJobClientManager;
    private final ClientHouseKeepingManager clientHouseKeepingManager;
    private final LoadBalance<String> agentLoadBalance;
    private final DataXProperties dataXProperties;

    public DataXServerManager(DataXProperties dataXProperties) {
        log.info("==================== DataXProperties =========================\n{}", JSON.toJSONString(dataXProperties, true));
        this.dataXProperties = dataXProperties;
        NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(this.dataXProperties.getTransport().getPort());
        this.transportServer = new NettyTransportServer(serverConfig);

        this.clientHouseKeepingManager = new ClientHouseKeepingManager(this);
        this.transportServer.registerChannelEventListener(this.clientHouseKeepingManager);

        //register processors
        ClientManagerProcessor clientManagerProcessor = new ClientManagerProcessor(this);
        this.transportServer.registerProcessor(RequestCodeConstants.AGENT_HEARTBEAT_CODE, clientManagerProcessor);
        this.transportServer.registerProcessor(RequestCodeConstants.CORE_JOB_HEARTBEAT_CODE, clientManagerProcessor);

        CoreJobProcessor coreJobProcessor = new CoreJobProcessor(this);
        this.transportServer.registerProcessor(RequestCodeConstants.REGISTER_DATAX_JOB_CODE, coreJobProcessor);
        this.transportServer.registerProcessor(RequestCodeConstants.DATAX_JOB_REPORT_CODE, coreJobProcessor);


        this.agentClientManager = new AgentClientManager();
        this.coreJobClientManager = new CoreJobClientManager();
        this.agentLoadBalance = new RoundRobinAgentLoadBalance();
    }


    @PostConstruct
    @Override
    public void start() {
        this.transportServer.start();
        this.clientHouseKeepingManager.start();
    }


    @Override
    public void shutdown(boolean shutdownNow) {
        this.clientHouseKeepingManager.shutdown(shutdownNow);
        this.transportServer.shutdown(shutdownNow);
    }

    public NettyTransportServer getTransportServer() {
        return transportServer;
    }

    public AgentClientManager getAgentClientManager() {
        return agentClientManager;
    }

    public CoreJobClientManager getCoreJobClientManager() {
        return coreJobClientManager;
    }

    public DataXProperties getDataXProperties() {
        return dataXProperties;
    }

    public String selectAgentClientId() {
        return this.agentLoadBalance.select(this.agentClientManager.getAliveClientIds());
    }


    public Packet callClient(ClientType clientType, String clientId, Packet request) throws InterruptedException, TimeoutException, TransportSendException {
        Channel channel;
        switch (clientType) {
            case AGENT:
                channel = this.agentClientManager.getChannel(clientId);
                break;
            case CORE_JOB:
                channel = this.coreJobClientManager.getChannel(clientId);
                break;
            default:
                return null;
        }
        return callClient(channel, request);
    }


    public Packet callClient(Channel channel, Packet request) throws InterruptedException, TimeoutException, TransportSendException {
        return this.transportServer.sendSync(channel, request, 10, TimeUnit.SECONDS);
    }


    public void callbackWhenJobFinished(long jobId, DataXJobReportInfo reportInfo) {
        String reportUrl = this.dataXProperties.getJob().getReportUrl();

        DataXJobReportRequest requestBody = new DataXJobReportRequest();
        requestBody.setJobId(jobId);
        requestBody.setReportInfo(reportInfo);

        Request req = new Request.Builder()
            .url(reportUrl)
            .post(RequestBody.create(MediaType.get("application/json"), JSON.toJSONString(requestBody)))
            .build();

        try {
            RetryUtil.executeWithRetry(() -> {
                Response response = OkHttpClientUtils.getHttpClient().newCall(req).execute();
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        JSONObject data = JSON.parseObject(response.body().string());
                        if (data.getIntValue("code") != 0) {
                            throw new RuntimeException("failed, resp: " + data.toJSONString());
                        }
                    } else {
                        throw new RuntimeException("failed, resp: " + response.body().string());
                    }
                }
                return null;
            }, 3, 10, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    @PreDestroy
    public void close() {
        shutdown(false);
    }

}
