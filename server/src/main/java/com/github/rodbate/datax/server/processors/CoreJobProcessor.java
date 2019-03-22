package com.github.rodbate.datax.server.processors;

import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.enums.DataXJobState;
import com.github.rodbate.datax.common.protocol.request.DataXJobReportRequest;
import com.github.rodbate.datax.common.protocol.request.RegisterDataXJobRequest;
import com.github.rodbate.datax.common.report.DataXJobReportInfo;
import com.github.rodbate.datax.transport.netty.NettyTransportRequestProcessor;
import com.github.rodbate.datax.transport.protocol.Packet;
import com.github.rodbate.datax.server.DataXServerManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:17
 */
@Slf4j
public class CoreJobProcessor implements NettyTransportRequestProcessor {

    private final DataXServerManager dataXServerManager;

    public CoreJobProcessor(DataXServerManager dataXServerManager) {
        this.dataXServerManager = dataXServerManager;
    }


    @Override
    public Packet process(ChannelHandlerContext ctx, Packet request) {
        switch (request.getCode()) {
            case RequestCodeConstants.REGISTER_DATAX_JOB_CODE:
                //register job
                return registerDataXJob(ctx, request);
            case RequestCodeConstants.DATAX_JOB_REPORT_CODE:
                //datax job report
                return reportJob(ctx, request);
        }
        return null;
    }


    private Packet registerDataXJob(ChannelHandlerContext ctx, Packet request) {
        RegisterDataXJobRequest registerDataXJobRequest = RegisterDataXJobRequest.decode(request.getBody(), RegisterDataXJobRequest.class);
        final long jobId = registerDataXJobRequest.getJobId();
        this.dataXServerManager.getCoreJobClientManager().registerJob(jobId, ctx.channel());
        return Packet.createSuccessResponsePacket();
    }

    private Packet reportJob(ChannelHandlerContext ctx, Packet request) {
        DataXJobReportRequest dataXJobReportRequest = DataXJobReportRequest.decode(request.getBody(), DataXJobReportRequest.class);
        long jobId = dataXJobReportRequest.getJobId();
        DataXJobReportInfo reportInfo = dataXJobReportRequest.getReportInfo();
        this.dataXServerManager.getCoreJobClientManager().reportJobInfo(jobId, reportInfo);
        if (DataXJobState.fromState(reportInfo.getState()).isFinished()) {
            try {
                this.dataXServerManager.callbackWhenJobFinished(jobId, reportInfo);
            } catch (Throwable ex) {
                //swallow ex
                log.error(ex.getMessage(), ex);
            }
        }
        return Packet.createSuccessResponsePacket();
    }


}
