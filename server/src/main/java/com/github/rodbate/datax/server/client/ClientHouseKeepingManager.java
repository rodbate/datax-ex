package com.github.rodbate.datax.server.client;

import com.github.rodbate.datax.common.service.IService;
import com.github.rodbate.datax.transport.common.NamedThreadFactory;
import com.github.rodbate.datax.transport.netty.ChannelEvent;
import com.github.rodbate.datax.transport.netty.ChannelEventListener;
import com.github.rodbate.datax.server.DataXServerManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 9:36
 */
@Slf4j
public class ClientHouseKeepingManager implements ChannelEventListener, IService {

    private final ScheduledExecutorService houseKeepingExecutor;
    private DataXServerManager dataXServerManager;

    public ClientHouseKeepingManager(DataXServerManager dataXServerManager) {
        this.dataXServerManager = dataXServerManager;
        houseKeepingExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("client-house-keeping"));
    }


    @Override
    public void onChannelEvent(ChannelEvent channelEvent) throws Exception {
        switch (channelEvent.getChannelEventType()) {
            case IDLE:
            case CLOSE:
            case EXCEPTION:
                handleInactiveChannelEvent(channelEvent);
                break;
            default:
                break;
        }
    }

    private void handleInactiveChannelEvent(ChannelEvent channelEvent) {
        log.info("channel event[{}] triggered!", channelEvent.getChannelEventType());
        this.dataXServerManager.getAgentClientManager().handleInActiveChannels(channelEvent);
        this.dataXServerManager.getCoreJobClientManager().handleInActiveChannels(channelEvent);
    }


    @Override
    public void start() {
        this.houseKeepingExecutor.scheduleAtFixedRate(this::scanClientChannels, 50, 10000, TimeUnit.MILLISECONDS);
    }


    private void scanClientChannels() {
        try {
            this.dataXServerManager.getAgentClientManager().handleExpiredChannels();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        try {
            this.dataXServerManager.getCoreJobClientManager().handleExpiredChannels();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void shutdown(boolean shutdownNow) {
        IService.closeExecutorService(this.houseKeepingExecutor, shutdownNow, log);
    }

}
