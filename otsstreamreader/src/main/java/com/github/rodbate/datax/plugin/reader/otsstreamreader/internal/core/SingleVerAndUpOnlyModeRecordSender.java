package com.github.rodbate.datax.plugin.reader.otsstreamreader.internal.core;

import com.github.rodbate.datax.common.element.Record;
import com.github.rodbate.datax.common.element.StringColumn;
import com.github.rodbate.datax.common.plugin.RecordSender;
import com.github.rodbate.datax.plugin.reader.otsstreamreader.internal.utils.ColumnValueTransformHelper;
import com.github.rodbate.datax.plugin.reader.otsstreamreader.internal.OTSStreamReaderException;
import com.alicloud.openservices.tablestore.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该输出模式假设用户对数据只有Put和Update操作，无Delete操作，且没有使用多版本。
 * 在该种模式下，会整行输出数据，用户必须指定需要导出的列的列名，输出的数据样例如下：
 * | pk1 | pk2 | col1 | col2 | col3 | sequence id |
 * | --- | --- | ---- | ---- | ---- | ----------- |
 * |  a  |  b  |  c1  | null | null | 001         |
 *
 * 注意：删除整行，删除某列（某个版本或所有），这些增量信息都会被忽略。
 */
public class SingleVerAndUpOnlyModeRecordSender implements IStreamRecordSender {

    private final RecordSender dataxRecordSender;
    private String shardId;
    private final boolean isExportSequenceInfo;
    private List<String> columnNames;

    public SingleVerAndUpOnlyModeRecordSender(RecordSender dataxRecordSender, String shardId, boolean isExportSequenceInfo, List<String> columnNames) {
        this.dataxRecordSender = dataxRecordSender;
        this.shardId = shardId;
        this.isExportSequenceInfo = isExportSequenceInfo;
        this.columnNames = columnNames;
    }

    @Override
    public void sendToDatax(StreamRecord streamRecord) {
        String sequenceInfo = getSequenceInfo(streamRecord);
        switch (streamRecord.getRecordType()) {
            case PUT:
            case UPDATE:
                sendToDatax(streamRecord.getPrimaryKey(), streamRecord.getColumns(), sequenceInfo);
                break;
            case DELETE:
                break;
            default:
                throw new OTSStreamReaderException("Unknown stream record type: " + streamRecord.getRecordType() + ".");
        }
    }

    private void sendToDatax(PrimaryKey primaryKey, List<RecordColumn> columns, String sequenceInfo) {
        Record line = dataxRecordSender.createRecord();

        Map<String, Object> map = new HashMap<String, Object>();
        for (PrimaryKeyColumn pkCol : primaryKey.getPrimaryKeyColumns()) {
            map.put(pkCol.getName(), pkCol.getValue());
        }

        for (RecordColumn recordColumn : columns) {
            if (recordColumn.getColumnType().equals(RecordColumn.ColumnType.PUT)) {
                map.put(recordColumn.getColumn().getName(), recordColumn.getColumn().getValue());
            }
        }

        boolean findColumn  = false;

        for (String colName : columnNames) {
            Object value = map.get(colName);
            if (value != null) {
                findColumn = true;
                if (value instanceof ColumnValue) {
                    line.addColumn(ColumnValueTransformHelper.otsColumnValueToDataxColumn((ColumnValue) value));
                } else {
                    line.addColumn(ColumnValueTransformHelper.otsPrimaryKeyValueToDataxColumn((PrimaryKeyValue) value));
                }
            } else {
                line.addColumn(new StringColumn(null));
            }
        }

        if (!findColumn) {
            return;
        }

        if (isExportSequenceInfo) {
            line.addColumn(new StringColumn(sequenceInfo));
        }
        synchronized (dataxRecordSender) {
            dataxRecordSender.sendToWriter(line);
        }
    }

    private String getSequenceInfo(StreamRecord streamRecord) {
        int epoch = streamRecord.getSequenceInfo().getEpoch();
        long timestamp = streamRecord.getSequenceInfo().getTimestamp();
        int rowIdx = streamRecord.getSequenceInfo().getRowIndex();
        String sequenceId = String.format("%010d_%020d_%010d_%s", epoch, timestamp, rowIdx, shardId);
        return sequenceId;
    }
}
