package com.github.rodbate.datax.server.processors;

import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.protocol.request.HeartbeatRequest;
import com.github.rodbate.datax.common.util.TransportUtil;
import com.github.rodbate.datax.transport.netty.NettyTransportRequestProcessor;
import com.github.rodbate.datax.transport.protocol.Packet;
import com.github.rodbate.datax.server.DataXServerManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 11:51
 */
public class ClientManagerProcessor implements NettyTransportRequestProcessor {

    private final DataXServerManager dataXServerManager;

    public ClientManagerProcessor(DataXServerManager dataXServerManager) {
        this.dataXServerManager = dataXServerManager;
    }

    @Override
    public Packet process(ChannelHandlerContext ctx, Packet request) {
        switch (request.getCode()) {
            case RequestCodeConstants.AGENT_HEARTBEAT_CODE:
                return handleAgentHeartbeat(ctx, request);
            case RequestCodeConstants.CORE_JOB_HEARTBEAT_CODE:
                return handleCoreJobHeartbeat(ctx, request);
            default:
                break;
        }
        return null;
    }


    private Packet handleAgentHeartbeat(ChannelHandlerContext ctx, Packet request) {
        HeartbeatRequest heartbeatRequest = decodeRequest(ctx, request);
        this.dataXServerManager.getAgentClientManager().registerClient(heartbeatRequest.getClientId(), ctx.channel());
        return Packet.createSuccessResponsePacket();
    }

    private Packet handleCoreJobHeartbeat(ChannelHandlerContext ctx, Packet request) {
        HeartbeatRequest heartbeatRequest = decodeRequest(ctx, request);
        this.dataXServerManager.getCoreJobClientManager().registerClient(heartbeatRequest.getClientId(), ctx.channel());
        return Packet.createSuccessResponsePacket();
    }

    private HeartbeatRequest decodeRequest(ChannelHandlerContext ctx, Packet request) {
        HeartbeatRequest heartbeat = HeartbeatRequest.decode(request.getBody(), HeartbeatRequest.class);
        if (StringUtils.isBlank(heartbeat.getClientId())) {
            heartbeat.setClientId(TransportUtil.buildClientId((InetSocketAddress) ctx.channel().remoteAddress()));
        }
        return heartbeat;
    }
}
