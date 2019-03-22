package com.github.rodbate.datax.server.client;

import com.github.rodbate.datax.common.constant.CommonConstant;
import com.github.rodbate.datax.transport.netty.ChannelEvent;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 11:01
 */
public abstract class AbstractClientManager {

    private static final long CLIENT_CHANNEL_EXPIRE_MILLIS = CommonConstant.HEARTBEAT_INTERVAL_MILLIS;
    //client id -> client channel info
    private final ConcurrentHashMap<String, ClientChannelInfo> clientMap = new ConcurrentHashMap<>(16);
    private final Object clientLock = new Object();
    protected Logger log = LoggerFactory.getLogger(getClass());


    public void registerClient(String clientId, Channel channel) {
        boolean isNew = false;
        ClientChannelInfo clientChannelInfo = clientMap.get(clientId);
        if (clientChannelInfo == null) {
            isNew = true;
            clientChannelInfo = new ClientChannelInfo(clientId, channel);
            ClientChannelInfo preClientChannelInfo = clientMap.putIfAbsent(clientId, clientChannelInfo);
            if (preClientChannelInfo != null) {
                isNew = false;
                clientChannelInfo = preClientChannelInfo;
            }
        }

        if (clientChannelInfo.getChannel() != channel) {
            log.info("{} - update old channel[{}] to new channel[{}] with the same clientId[{}]",
                getClass().getSimpleName(), clientChannelInfo.getChannel(), channel, clientId);
            clientChannelInfo = new ClientChannelInfo(clientId, channel);
            //update channel info
            isNew = true;
            clientMap.put(clientId, clientChannelInfo);
        } else {
            //update timestamp
            clientChannelInfo.setLastUpdateTimestamp(System.currentTimeMillis());
        }

        log.info("{} - register client, clientId={}, channel={}, isNew={}", getClass().getSimpleName(), clientId, channel, isNew);

        postRegisterClient(clientChannelInfo, isNew);
    }


    public void unregisterClient(String clientId) {
        ClientChannelInfo channelInfo = clientMap.remove(clientId);
        if (channelInfo == null) {
            log.warn("cannot find client channel for clientId: {}", clientId);
        } else {
            log.info("{} - unregister client, clientId={}, channel={}", getClass().getSimpleName(), channelInfo.getClientId(), channelInfo.getChannel());
        }
        postUnregisterClient(channelInfo);
    }


    protected void postRegisterClient(ClientChannelInfo clientChannelInfo, boolean isNew) {

    }

    protected void postUnregisterClient(ClientChannelInfo channelInfo) {

    }


    public Channel getChannel(String clientId) {
        ClientChannelInfo clientChannelInfo = clientMap.get(clientId);
        if (clientChannelInfo == null) {
            throw new RuntimeException("cannot find client channel for clientId: " + clientId);
        }
        return clientChannelInfo.getChannel();
    }


    public void handleExpiredChannels() {
        synchronized (clientLock) {
            Iterator<Map.Entry<String, ClientChannelInfo>> iterator = clientMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ClientChannelInfo> entry = iterator.next();
                if (System.currentTimeMillis() - entry.getValue().getLastUpdateTimestamp() > CLIENT_CHANNEL_EXPIRE_MILLIS) {
                    log.info("{} - remove expired agent client channel <{}>", getClass().getSimpleName(), entry.getValue().getChannel());
                    doExpiredChannel(entry.getValue());
                    iterator.remove();
                }
            }
        }
    }


    public void handleInActiveChannels(ChannelEvent channelEvent) {
        synchronized (clientLock) {
            Iterator<Map.Entry<String, ClientChannelInfo>> iterator = clientMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ClientChannelInfo> entry = iterator.next();
                if (channelEvent.getSource() == entry.getValue().getChannel()) {
                    log.info("{} - remove inactive agent client channel, cause channel event {}", getClass().getSimpleName(), channelEvent);
                    doInactiveChannel(entry.getValue());
                    iterator.remove();
                }
            }
        }
    }


    private void doExpiredChannel(ClientChannelInfo clientChannelInfo) {
        try {
            doExpiredChannel0(clientChannelInfo);
        } catch (Throwable ex) {
            log.error("doExpiredChannel exception, cause: " + ex.getMessage(), ex);
        }
    }

    protected void doExpiredChannel0(ClientChannelInfo clientChannelInfo) {

    }


    private void doInactiveChannel(ClientChannelInfo clientChannelInfo) {
        try {
            doInactiveChannel0(clientChannelInfo);
        } catch (Throwable ex) {
            log.error("doInactiveChannel exception, cause: " + ex.getMessage(), ex);
        }
    }

    protected void doInactiveChannel0(ClientChannelInfo clientChannelInfo) {

    }

}
