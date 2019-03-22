package com.github.rodbate.datax.core.transport.exchanger;

import com.github.rodbate.datax.common.element.Record;
import com.github.rodbate.datax.common.exception.CommonErrorCode;
import com.github.rodbate.datax.common.exception.DataXException;
import com.github.rodbate.datax.common.plugin.RecordReceiver;
import com.github.rodbate.datax.common.plugin.RecordSender;
import com.github.rodbate.datax.common.plugin.TaskPluginCollector;
import com.github.rodbate.datax.common.util.Configuration;
import com.github.rodbate.datax.core.transport.channel.Channel;
import com.github.rodbate.datax.core.transport.record.TerminateRecord;
import com.github.rodbate.datax.core.util.FrameworkErrorCode;
import com.github.rodbate.datax.core.util.container.CoreConstant;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferedRecordExchanger implements RecordSender, RecordReceiver {

    private static Class<? extends Record> RECORD_CLASS;
    protected final int byteCapacity;
    private final Channel channel;
    private final Configuration configuration;
    private final List<Record> buffer;
    private final AtomicInteger memoryBytes = new AtomicInteger(0);
    private final TaskPluginCollector pluginCollector;
    private int bufferSize;
    private int bufferIndex = 0;
    private volatile boolean shutdown = false;

    @SuppressWarnings("unchecked")
    public BufferedRecordExchanger(final Channel channel, final TaskPluginCollector pluginCollector) {
        assert null != channel;
        assert null != channel.getConfiguration();

        this.channel = channel;
        this.pluginCollector = pluginCollector;
        this.configuration = channel.getConfiguration();

        this.bufferSize = configuration
            .getInt(CoreConstant.DATAX_CORE_TRANSPORT_EXCHANGER_BUFFERSIZE);
        this.buffer = new ArrayList<>(bufferSize);

        //channel的queue默认大小为8M，原来为64M
        this.byteCapacity = configuration.getInt(
            CoreConstant.DATAX_CORE_TRANSPORT_CHANNEL_CAPACITY_BYTE, 8 * 1024 * 1024);

        try {
            BufferedRecordExchanger.RECORD_CLASS = ((Class<? extends Record>) Class
                .forName(configuration.getString(
                    CoreConstant.DATAX_CORE_TRANSPORT_RECORD_CLASS,
                    "com.globalegrow.datax.core.transport.record.DefaultRecord")));
        } catch (Exception e) {
            throw DataXException.asDataXException(
                FrameworkErrorCode.CONFIG_ERROR, e);
        }
    }

    @Override
    public Record createRecord() {
        try {
            return BufferedRecordExchanger.RECORD_CLASS.newInstance();
        } catch (Exception e) {
            throw DataXException.asDataXException(
                FrameworkErrorCode.CONFIG_ERROR, e);
        }
    }

    @Override
    public void sendToWriter(Record record) {
        if (shutdown) {
            throw DataXException.asDataXException(CommonErrorCode.SHUT_DOWN_TASK, "");
        }

        Validate.notNull(record, "record不能为空.");

        if (record.getMemorySize() > this.byteCapacity) {
            this.pluginCollector.collectDirtyRecord(record, new Exception(String.format("单条记录超过大小限制，当前限制为:%s", this.byteCapacity)));
            return;
        }

        boolean isFull = (this.bufferIndex >= this.bufferSize || this.memoryBytes.get() + record.getMemorySize() > this.byteCapacity);
        if (isFull) {
            flush();
        }

        this.buffer.add(record);
        this.bufferIndex++;
        memoryBytes.addAndGet(record.getMemorySize());
    }

    @Override
    public void flush() {
        if (shutdown) {
            throw DataXException.asDataXException(CommonErrorCode.SHUT_DOWN_TASK, "");
        }
        this.channel.pushAll(this.buffer);
        this.buffer.clear();
        this.bufferIndex = 0;
        this.memoryBytes.set(0);
    }

    @Override
    public void terminate() {
        if (shutdown) {
            throw DataXException.asDataXException(CommonErrorCode.SHUT_DOWN_TASK, "");
        }
        flush();
        this.channel.pushTerminate(TerminateRecord.get());
    }

    @Override
    public Record getFromReader() {
        if (shutdown) {
            throw DataXException.asDataXException(CommonErrorCode.SHUT_DOWN_TASK, "");
        }
        boolean isEmpty = (this.bufferIndex >= this.buffer.size());
        if (isEmpty) {
            receive();
        }

        Record record = this.buffer.get(this.bufferIndex++);
        if (record instanceof TerminateRecord) {
            record = null;
        }
        return record;
    }

    @Override
    public void shutdown() {
        shutdown = true;
        try {
            buffer.clear();
            channel.clear();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void receive() {
        this.channel.pullAll(this.buffer);
        this.bufferIndex = 0;
        this.bufferSize = this.buffer.size();
    }
}
