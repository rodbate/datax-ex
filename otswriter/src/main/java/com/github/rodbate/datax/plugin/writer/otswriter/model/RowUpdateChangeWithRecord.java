package com.github.rodbate.datax.plugin.writer.otswriter.model;

import com.github.rodbate.datax.common.element.Record;

public class RowUpdateChangeWithRecord extends com.aliyun.openservices.ots.model.RowUpdateChange implements WithRecord {

    private Record record;

    public RowUpdateChangeWithRecord(String tableName) {
        super(tableName);
    }

    @Override
    public Record getRecord() {
        return record;
    }

    @Override
    public void setRecord(Record record) {
        this.record = record;
    }
}
