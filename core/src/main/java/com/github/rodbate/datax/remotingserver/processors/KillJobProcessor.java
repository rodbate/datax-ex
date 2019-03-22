package com.github.rodbate.datax.remotingserver.processors;

import com.github.rodbate.datax.remotingserver.common.ApplicationManager;
import com.github.rodbate.datax.transport.netty.NettyTransportRequestProcessor;
import com.github.rodbate.datax.transport.protocol.Packet;
import io.netty.channel.ChannelHandlerContext;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 16:57
 */
public class KillJobProcessor implements NettyTransportRequestProcessor {

    private final ApplicationManager applicationManager;

    public KillJobProcessor(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    @Override
    public Packet process(ChannelHandlerContext ctx, Packet request) {
        this.applicationManager.getJobContainer().killJob();
        return Packet.createSuccessResponsePacket();
    }


}
