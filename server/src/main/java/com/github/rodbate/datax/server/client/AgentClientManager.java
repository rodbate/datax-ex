package com.github.rodbate.datax.server.client;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 9:56
 */
@Slf4j
public class AgentClientManager extends AbstractClientManager {

    private final Set<String> aliveClientIds = Collections.synchronizedSet(new HashSet<>());

    @Override
    protected void postRegisterClient(ClientChannelInfo clientChannelInfo, boolean isNew) {
        if (isNew) {
            aliveClientIds.add(clientChannelInfo.getClientId());
        }
    }

    @Override
    protected void postUnregisterClient(ClientChannelInfo channelInfo) {
        clearAgents(channelInfo);
    }


    @Override
    protected void doExpiredChannel0(ClientChannelInfo clientChannelInfo) {
        clearAgents(clientChannelInfo);
    }

    @Override
    protected void doInactiveChannel0(ClientChannelInfo clientChannelInfo) {
        clearAgents(clientChannelInfo);
    }

    private void clearAgents(ClientChannelInfo clientChannelInfo) {
        if (clientChannelInfo != null) {
            aliveClientIds.remove(clientChannelInfo.getClientId());
        }
    }

    public List<String> getAliveClientIds() {
        return new ArrayList<>(this.aliveClientIds);
    }

}
