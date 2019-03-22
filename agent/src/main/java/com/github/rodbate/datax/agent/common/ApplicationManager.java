package com.github.rodbate.datax.agent.common;

import com.github.rodbate.datax.agent.heartbeat.HeartbeatService;
import com.github.rodbate.datax.agent.processors.StartDataXJobProcessor;
import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.service.IService;
import com.github.rodbate.datax.transport.netty.NettyTransportClient;
import com.github.rodbate.datax.transport.netty.config.NettyClientConfig;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 15:50
 */
public class ApplicationManager implements IService {

    private final NettyTransportClient nettyTransportClient;
    private final HeartbeatService heartbeatService;

    public ApplicationManager() {
        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        this.nettyTransportClient = new NettyTransportClient(nettyClientConfig, "datax-agent");

        //register agent processors
        this.nettyTransportClient.registerProcessor(RequestCodeConstants.START_DATAX_JOB_CODE, new StartDataXJobProcessor());

        this.heartbeatService = new HeartbeatService(this);
    }

    public NettyTransportClient getNettyTransportClient() {
        return nettyTransportClient;
    }

    @Override
    public void start() {
        this.nettyTransportClient.start();
        this.heartbeatService.start();
    }

    @Override
    public void shutdown(boolean shutdownNow) {
        this.heartbeatService.shutdown(shutdownNow);
        this.nettyTransportClient.shutdown(shutdownNow);
    }

}
