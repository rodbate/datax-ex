package com.github.rodbate.datax.agent.processors;

import com.github.rodbate.datax.agent.utils.ShellUtils;
import com.github.rodbate.datax.common.enums.DataXExecuteMode;
import com.github.rodbate.datax.common.protocol.request.StartDataXJobRequest;
import com.github.rodbate.datax.transport.netty.NettyTransportRequestProcessor;
import com.github.rodbate.datax.transport.protocol.Packet;
import io.netty.channel.ChannelHandlerContext;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 15:40
 */
public class StartDataXJobProcessor implements NettyTransportRequestProcessor {

    @Override
    public Packet process(ChannelHandlerContext ctx, Packet request) {
        StartDataXJobRequest dataXJobRequest = StartDataXJobRequest.decode(request.getBody(), StartDataXJobRequest.class);
        ShellUtils.startDataXJob(DataXExecuteMode.STANDALONE, dataXJobRequest.getJobId(), dataXJobRequest.getJobConfUrl());
        return Packet.createSuccessResponsePacket();
    }

}
