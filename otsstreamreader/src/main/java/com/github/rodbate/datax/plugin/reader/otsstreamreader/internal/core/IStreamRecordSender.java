package com.github.rodbate.datax.plugin.reader.otsstreamreader.internal.core;

import com.alicloud.openservices.tablestore.model.StreamRecord;

public interface IStreamRecordSender {

    void sendToDatax(StreamRecord streamRecord);

}
