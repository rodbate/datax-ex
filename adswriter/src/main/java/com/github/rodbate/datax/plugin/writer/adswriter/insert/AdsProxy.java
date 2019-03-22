package com.github.rodbate.datax.plugin.writer.adswriter.insert;

import com.github.rodbate.datax.common.plugin.RecordReceiver;

import java.sql.Connection;

public interface AdsProxy {
    void startWriteWithConnection(RecordReceiver recordReceiver, Connection connection,
                                                  int columnNumber);

    void closeResource();
}
