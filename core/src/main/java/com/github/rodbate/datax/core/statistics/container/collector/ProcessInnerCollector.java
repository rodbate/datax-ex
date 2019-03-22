package com.github.rodbate.datax.core.statistics.container.collector;

import com.github.rodbate.datax.core.statistics.communication.Communication;
import com.github.rodbate.datax.core.statistics.communication.LocalTGCommunicationManager;

public class ProcessInnerCollector extends AbstractCollector {

    public ProcessInnerCollector(Long jobId) {
        super.setJobId(jobId);
    }

    @Override
    public Communication collectFromTaskGroup() {
        return LocalTGCommunicationManager.getJobCommunication();
    }

}
