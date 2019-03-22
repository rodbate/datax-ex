package com.github.rodbate.datax.server.client;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 9:30
 */
public class ClientChannelInfo {
    private final String clientId;
    private final Channel channel;
    private long lastUpdateTimestamp = System.currentTimeMillis();


    public ClientChannelInfo(String clientId, Channel channel) {
        Validate.isTrue(StringUtils.isNotBlank(clientId), "clientId require not empty");
        Validate.isTrue(channel != null, "channel require not null");
        this.clientId = clientId;
        this.channel = channel;
    }


    public String getClientId() {
        return clientId;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientChannelInfo that = (ClientChannelInfo) o;
        return clientId.equals(that.clientId) &&
            channel.equals(that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, channel);
    }
}
