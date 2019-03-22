package com.github.rodbate.datax.plugin.writer.otswriter.model;

import com.github.rodbate.datax.common.element.Record;

public interface WithRecord {
    Record getRecord();

    void setRecord(Record record);
}
