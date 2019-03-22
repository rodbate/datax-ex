package com.github.rodbate.datax.remotingserver.heartbeat;

import com.github.rodbate.datax.common.constant.CommonConstant;
import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.protocol.request.HeartbeatRequest;
import com.github.rodbate.datax.common.service.IService;
import com.github.rodbate.datax.remotingserver.common.ApplicationManager;
import com.github.rodbate.datax.transport.common.NamedThreadFactory;
import com.github.rodbate.datax.transport.exceptions.TransportConnectException;
import com.github.rodbate.datax.transport.exceptions.TransportSendException;
import com.github.rodbate.datax.transport.netty.NettyTransportClient;
import com.github.rodbate.datax.transport.protocol.Packet;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 16:48
 */
@Slf4j
public class HeartbeatService implements IService {

    private final ScheduledExecutorService executorService;
    private final ApplicationManager applicationManager;



    public HeartbeatService(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
        this.executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("core-heartbeat"));
    }


    private void sendHeartbeat() {
        NettyTransportClient transportClient = this.applicationManager.getNettyTransportClient();
        Packet heartbeatRequest = Packet.createRequestPacket(RequestCodeConstants.CORE_JOB_HEARTBEAT_CODE);
        HeartbeatRequest heartbeat = new HeartbeatRequest();
        heartbeatRequest.setBody(HeartbeatRequest.encode(heartbeat));

        String dataxServerAddress = this.applicationManager.getDataxServerAddress();
        try {
            transportClient.sendSync(dataxServerAddress, heartbeatRequest, 5, TimeUnit.SECONDS);
            log.info("send heartbeat to server: {}", dataxServerAddress);
        } catch (TransportConnectException e) {
            log.error("heartbeat connect exception", e);
        } catch (InterruptedException e) {
            log.error("heartbeat interrupt exception", e);
        } catch (TimeoutException e) {
            log.error("heartbeat timeout exception", e);
        } catch (TransportSendException e) {
            log.error("heartbeat send exception", e);
        } catch (Throwable unknown) {
            log.error("unknown exception", unknown);
        }
    }


    @Override
    public void start() {
        this.executorService.scheduleAtFixedRate(this::sendHeartbeat, 100, CommonConstant.HEARTBEAT_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown(boolean shutdownNow) {
        IService.closeExecutorService(this.executorService, shutdownNow, log);
    }

}
