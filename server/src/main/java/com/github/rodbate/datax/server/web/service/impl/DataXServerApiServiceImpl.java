package com.github.rodbate.datax.server.web.service.impl;

import com.github.rodbate.datax.common.constant.RequestCodeConstants;
import com.github.rodbate.datax.common.enums.DataXJobState;
import com.github.rodbate.datax.common.protocol.request.StartDataXJobRequest;
import com.github.rodbate.datax.common.report.DataXJobReportInfo;
import com.github.rodbate.datax.common.util.InetUtil;
import com.github.rodbate.datax.common.web.ReturnCode;
import com.github.rodbate.datax.common.web.dto.resp.GetJobConfigResp;
import com.github.rodbate.datax.server.client.ClientType;
import com.github.rodbate.datax.server.config.web.BootApplicationRunListener;
import com.github.rodbate.datax.server.exceptions.DataXServerException;
import com.github.rodbate.datax.server.web.dto.req.StartJobReq;
import com.github.rodbate.datax.server.web.service.DataXServerApiService;
import com.github.rodbate.datax.transport.exceptions.TransportSendException;
import com.github.rodbate.datax.transport.protocol.Packet;
import com.github.rodbate.datax.server.DataXServerManager;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 15:05
 */
@Service
public class DataXServerApiServiceImpl implements DataXServerApiService {

    //job id -> job conf
    private final ConcurrentHashMap<Long, String> jobIdToJobConfMap = new ConcurrentHashMap<>(128);

    @Autowired
    private DataXServerManager dataXServerManager;


    @Override
    public Mono<Void> startJob(Mono<StartJobReq> req) {
        return req.flatMap(startJobReq -> {
            final long jobId = startJobReq.getJobId();

            //check job status
            dataXServerManager.getCoreJobClientManager().checkJobState(jobId, true, reportInfo -> this.writeBackJobInfo(jobId, reportInfo));

            this.jobIdToJobConfMap.put(startJobReq.getJobId(), startJobReq.getJobConf());
            String agentClientId = dataXServerManager.selectAgentClientId();
            if (StringUtils.isBlank(agentClientId)) {
                throw new DataXServerException(ReturnCode.NO_ALIVE_DATAX_AGENT);
            }
            StartDataXJobRequest request = new StartDataXJobRequest();
            request.setJobId(startJobReq.getJobId());
            request.setJobConfUrl(buildJobUrl(request.getJobId()));

            Packet requestPacket = Packet.createRequestPacket(RequestCodeConstants.START_DATAX_JOB_CODE);
            requestPacket.setBody(StartDataXJobRequest.encode(request));

            try {
                Packet response = dataXServerManager.callClient(ClientType.AGENT, agentClientId, requestPacket);
                if (!response.isSuccessResponse()) {
                    throw new RuntimeException(String.format("failed to start job, jobId=%d, error=%s", startJobReq.getJobId(), response.getRemark()));
                }
            } catch (InterruptedException | TimeoutException | TransportSendException e) {
                throw new DataXServerException(ReturnCode.INTERNAL_SERVER_ERROR, e);
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> stopJob(Long jobId) {
        return Mono.fromRunnable(() -> {

            //check job status
            dataXServerManager.getCoreJobClientManager().checkJobState(jobId, false, reportInfo -> this.writeBackJobInfo(jobId, reportInfo));

            Packet requestPacket = Packet.createRequestPacket(RequestCodeConstants.KILL_DATAX_JOB_CODE);

            Channel channel = dataXServerManager.getCoreJobClientManager().getChannel(jobId);
            if (channel == null) {
                throw new DataXServerException(ReturnCode.NOT_FOUND_JOB, jobId);
            }

            dataXServerManager.getCoreJobClientManager().updateJobState(jobId, DataXJobState.KILLING);
            try {
                Packet response = dataXServerManager.callClient(channel, requestPacket);
                if (!response.isSuccessResponse()) {
                    throw new RuntimeException(String.format("failed to stop job, jobId=%d, error=%s", jobId, response.getRemark()));
                }
            } catch (InterruptedException | TimeoutException | TransportSendException e) {
                throw new DataXServerException(ReturnCode.INTERNAL_SERVER_ERROR, e);
            }
        });
    }


    @Override
    public Mono<GetJobConfigResp> getJobConfig(Long jobId) {
        return Mono.fromCallable(() -> {
            String jobConf = jobIdToJobConfMap.get(jobId);
            if (StringUtils.isBlank(jobConf)) {
                throw new DataXServerException(ReturnCode.NOT_FOUND);
            }
            return jobConf;
        }).map(GetJobConfigResp::new);
    }


    private void writeBackJobInfo(long jobId, DataXJobReportInfo reportInfo) {
        this.dataXServerManager.callbackWhenJobFinished(jobId, reportInfo);
    }


    private String buildJobUrl(long jobId) {
        String urlFormat = "http://%s:%d/datax/server/api/job/%d/config";
        String host = this.dataXServerManager.getDataXProperties().getTransport().getHost();
        if (StringUtils.isBlank(host)) {
            host = InetUtil.getLocalhostLanAddress().getHostAddress();
        }
        int webPort = BootApplicationRunListener.webServerPort;
        return String.format(urlFormat, host, webPort, jobId);
    }

}
