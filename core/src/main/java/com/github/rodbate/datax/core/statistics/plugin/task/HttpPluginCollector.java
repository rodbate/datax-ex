package com.github.rodbate.datax.core.statistics.plugin.task;

import com.github.rodbate.datax.common.constant.PluginType;
import com.github.rodbate.datax.common.element.Record;
import com.github.rodbate.datax.common.util.Configuration;
import com.github.rodbate.datax.core.statistics.communication.Communication;

/**
 * Created by jingxing on 14-9-9.
 */
public class HttpPluginCollector extends AbstractTaskPluginCollector {
    public HttpPluginCollector(Configuration configuration, Communication Communication,
                               PluginType type) {
        super(configuration, Communication, type);
    }

    @Override
    public void collectDirtyRecord(Record dirtyRecord, Throwable t,
                                   String errorMessage) {
        super.collectDirtyRecord(dirtyRecord, t, errorMessage);
    }

}
