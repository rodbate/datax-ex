package com.github.rodbate.datax.core.statistics.container.communicator.taskgroup;

import com.github.rodbate.datax.common.util.Configuration;
import com.github.rodbate.datax.core.statistics.communication.Communication;
import com.github.rodbate.datax.core.statistics.container.report.ProcessInnerReporter;

public class StandaloneTGContainerCommunicator extends AbstractTGContainerCommunicator {

    public StandaloneTGContainerCommunicator(Configuration configuration) {
        super(configuration);
        super.setReporter(new ProcessInnerReporter());
    }

    @Override
    public void report(Communication communication) {
        super.getReporter().reportTGCommunication(super.taskGroupId, communication);
    }

}
