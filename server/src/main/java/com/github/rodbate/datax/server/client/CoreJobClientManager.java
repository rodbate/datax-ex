package com.github.rodbate.datax.server.client;

import com.alibaba.fastjson.JSON;
import com.github.rodbate.datax.common.enums.DataXJobState;
import com.github.rodbate.datax.common.report.DataXJobReportInfo;
import com.github.rodbate.datax.common.util.TransportUtil;
import com.github.rodbate.datax.common.web.ReturnCode;
import com.github.rodbate.datax.server.exceptions.DataXServerException;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 10:57
 */
@Slf4j
public class CoreJobClientManager extends AbstractClientManager {

    //job id -> core job client id
    private final ConcurrentHashMap<Long, String> jobIdToClientIdMap = new ConcurrentHashMap<>(32);

    //core job client id -> job id list
    private final ConcurrentHashMap<String, Set<Long>> clientIdToJobIdsMap = new ConcurrentHashMap<>(16);

    //job id -> datax job report info
    private final ConcurrentHashMap<Long, DataXJobReportInfo> jobIdToJobReportInfoMap = new ConcurrentHashMap<>(32);
    private final Object jobReportLock = new Object();

    public void registerJob(long jobId, Channel channel) {
        log.info("register job, jobId={}", jobId);
        final String clientId = TransportUtil.buildClientId((InetSocketAddress) channel.remoteAddress());
        this.jobIdToClientIdMap.put(jobId, clientId);
        this.clientIdToJobIdsMap.computeIfAbsent(clientId, k -> Collections.synchronizedSet(new HashSet<>())).add(jobId);

        DataXJobReportInfo reportInfo = new DataXJobReportInfo();
        reportInfo.setState(DataXJobState.RUNNING.getState());
        this.jobIdToJobReportInfoMap.put(jobId, reportInfo);
    }


    public void reportJobInfo(long jobId, DataXJobReportInfo reportInfo) {
        log.info("report job info, jobId={}, reportInfo={}", jobId, JSON.toJSONString(reportInfo));
        if (DataXJobState.fromState(reportInfo.getState()).isRunning()) {
            this.jobIdToJobReportInfoMap.put(jobId, reportInfo);
        } else {
            this.jobIdToJobReportInfoMap.remove(jobId);
        }
    }

    @Override
    protected void doExpiredChannel0(ClientChannelInfo clientChannelInfo) {
        clearCoreJobClient(clientChannelInfo);
    }

    @Override
    protected void doInactiveChannel0(ClientChannelInfo clientChannelInfo) {
        clearCoreJobClient(clientChannelInfo);
    }

    public Channel getChannel(long jobId) {
        String clientId = this.jobIdToClientIdMap.get(jobId);
        if (StringUtils.isBlank(clientId)) {
            throw new RuntimeException("cannot find alive channel for jobId: " + jobId);
        }
        return getChannel(clientId);
    }

    public void updateJobState(long jobId, DataXJobState state) {
        synchronized (jobReportLock) {
            DataXJobReportInfo reportInfo = this.jobIdToJobReportInfoMap.get(jobId);
            if (reportInfo != null) {
                reportInfo.setState(state.getState());
            }
        }
    }

    private void clearCoreJobClient(ClientChannelInfo clientChannelInfo) {
        if (clientChannelInfo != null) {
            Set<Long> jobIds = this.clientIdToJobIdsMap.remove(clientChannelInfo.getClientId());
            if (jobIds != null) {
                for (long jobId : jobIds) {
                    this.jobIdToClientIdMap.remove(jobId);
                    this.jobIdToJobReportInfoMap.remove(jobId);
                }
            }
        }
    }



    public void checkJobState(long jobId, boolean startJob, Consumer<DataXJobReportInfo> jobFinishedCallback) {
        synchronized (jobReportLock) {
            DataXJobReportInfo reportInfo = this.jobIdToJobReportInfoMap.get(jobId);
            if (reportInfo != null) {
                DataXJobState dataXJobState = DataXJobState.fromState(reportInfo.getState());
                if (dataXJobState.isRunning()) {
                    //job is running
                    if (startJob) {
                        throw new DataXServerException(ReturnCode.JOB_IS_RUNNING, jobId);
                    } else {
                        //stop job
                        if (dataXJobState == DataXJobState.KILLING) {
                            throw new DataXServerException(ReturnCode.JOB_IS_KILLING, jobId);
                        }
                    }
                } else {
                    //job is finished
                    try {
                        jobFinishedCallback.accept(reportInfo);
                    } catch (Throwable ex) {
                        log.error("jobFinishedCallback exec exception, cause: " + ex.getMessage(), ex);
                    } finally {
                        this.jobIdToJobReportInfoMap.remove(jobId);
                    }
                }
            }
        }
    }

}
