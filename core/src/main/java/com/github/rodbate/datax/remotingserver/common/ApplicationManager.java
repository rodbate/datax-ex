package com.github.rodbate.datax.remotingserver.common;


import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.protocol.request.DataXJobReportRequest;
import com.github.rodbate.datax.common.protocol.request.RegisterDataXJobRequest;
import com.github.rodbate.datax.common.report.DataXJobReportInfo;
import com.github.rodbate.datax.common.service.IService;
import com.github.rodbate.datax.common.util.RetryUtil;
import com.github.rodbate.datax.core.Engine;
import com.github.rodbate.datax.core.job.JobContainer;
import com.github.rodbate.datax.core.statistics.communication.Communication;
import com.github.rodbate.datax.core.statistics.communication.CommunicationTool;
import com.github.rodbate.datax.remotingserver.heartbeat.HeartbeatService;
import com.github.rodbate.datax.remotingserver.processors.KillJobProcessor;
import com.github.rodbate.datax.transport.netty.NettyTransportClient;
import com.github.rodbate.datax.transport.netty.config.NettyClientConfig;
import com.github.rodbate.datax.transport.protocol.Packet;
import lombok.extern.slf4j.Slf4j;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 16:46
 */
@Slf4j
public class ApplicationManager implements IService {

    private final NettyTransportClient nettyTransportClient;
    private final HeartbeatService heartbeatService;
    private final String dataxServerAddress = System.getProperty("dataxServerAddress", "localhost:22000");
    private Engine engine;

    public ApplicationManager(Engine engine) {
        this.engine = engine;

        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        this.nettyTransportClient = new NettyTransportClient(nettyClientConfig, "datax-core");

        //register core job processors
        this.nettyTransportClient.registerProcessor(RequestCodeConstants.KILL_DATAX_JOB_CODE, new KillJobProcessor(this));


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

    public JobContainer getJobContainer() {
        return engine.getJobContainer();
    }


    public void registerJob(long jobId) {
        Packet requestPacket = Packet.createRequestPacket(RequestCodeConstants.REGISTER_DATAX_JOB_CODE);
        RegisterDataXJobRequest registerDataXJobRequest = new RegisterDataXJobRequest();
        registerDataXJobRequest.setJobId(jobId);
        requestPacket.setBody(RegisterDataXJobRequest.encode(registerDataXJobRequest));

        sendPacketToServer(requestPacket, "register job", jobId, true);
    }


    public void reportJob(long jobId, Communication communication, Throwable ex) {
        Packet requestPacket = Packet.createRequestPacket(RequestCodeConstants.DATAX_JOB_REPORT_CODE);
        DataXJobReportRequest dataXJobReportRequest = new DataXJobReportRequest();
        dataXJobReportRequest.setJobId(jobId);

        //wire job report info
        final DataXJobReportInfo reportInfo = new DataXJobReportInfo();
        reportInfo.setState(communication.getState().value());
        JobContainer jobContainer = engine.getJobContainer();
        if (jobContainer != null) {
            reportInfo.setStartTimestamp(jobContainer.getStartTimeStamp());
            reportInfo.setEndTimestamp(jobContainer.getEndTimeStamp());
            reportInfo.setStartTransferTimeStamp(jobContainer.getStartTransferTimeStamp());
            reportInfo.setEndTransferTimeStamp(jobContainer.getEndTransferTimeStamp());
        }

        reportInfo.setReadSucceedRecords(communication.getLongCounter(CommunicationTool.READ_SUCCEED_RECORDS));
        reportInfo.setReadSucceedBytes(communication.getLongCounter(CommunicationTool.READ_SUCCEED_BYTES));
        reportInfo.setReadFailedRecords(communication.getLongCounter(CommunicationTool.READ_FAILED_RECORDS));
        reportInfo.setReadFailedBytes(communication.getLongCounter(CommunicationTool.READ_FAILED_BYTES));

        reportInfo.setWriteSucceedRecords(communication.getLongCounter(CommunicationTool.WRITE_SUCCEED_RECORDS));
        reportInfo.setWriteSucceedBytes(communication.getLongCounter(CommunicationTool.WRITE_SUCCEED_BYTES));
        reportInfo.setWriteFailedRecords(communication.getLongCounter(CommunicationTool.WRITE_FAILED_RECORDS));
        reportInfo.setWriteFailedBytes(communication.getLongCounter(CommunicationTool.WRITE_FAILED_BYTES));

        if (ex != null) {
            reportInfo.setErrorMsg(ex.getMessage());
        }

        dataXJobReportRequest.setReportInfo(reportInfo);
        requestPacket.setBody(DataXJobReportRequest.encode(dataXJobReportRequest));

        sendPacketToServer(requestPacket, "report job", jobId, false);
    }


    private void sendPacketToServer(Packet packet, String action, long jobId, boolean throwIfFailed) {
        try {
            Packet rs = RetryUtil.executeWithRetry(() -> this.nettyTransportClient.sendSync(this.dataxServerAddress, packet), 3, 1000, false);
            if (!rs.isSuccessResponse()) {
                log.error(String.format("failed to %s, jobId: %d, cause: %s", action, jobId, rs.getRemark()));
                if (throwIfFailed) {
                    throw new RuntimeException(String.format("failed to %s, jobId: %d, cause: %s", action, jobId, rs.getRemark()));
                }
            }
        } catch (Exception e) {
            log.error(String.format("failed to %s, jobId: %d, cause: %s", action, jobId, e.getMessage()), e);
            if (throwIfFailed) {
                throw new RuntimeException(String.format("failed to %s, jobId: %d, cause: %s", action, jobId, e.getMessage()), e);
            }
        }
    }


    public String getDataxServerAddress() {
        return dataxServerAddress;
    }
}
