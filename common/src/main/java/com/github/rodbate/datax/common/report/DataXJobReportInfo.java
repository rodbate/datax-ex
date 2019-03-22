package com.github.rodbate.datax.common.report;


import com.github.rodbate.datax.common.enums.DataXJobState;
import lombok.Getter;
import lombok.Setter;

/**
 * User: rodbate
 * Date: 2019/3/7
 * Time: 9:55
 */
@Getter
@Setter
public class DataXJobReportInfo {
    /**
     * 任务状态
     * {@link DataXJobState}
     */
    private int state;
    /**
     * 任务开始时间戳 (毫秒)
     */
    private long startTimestamp;
    /**
     * 任务结束时间戳 (毫秒)
     */
    private long endTimestamp;
    /**
     * 任务数据开始传输时间戳 (毫秒)
     */
    private long startTransferTimeStamp;
    /**
     * 任务数据结束传输时间戳 (毫秒)
     */
    private long endTransferTimeStamp;

    /**
     * 读出成功的记录数
     */
    private long readSucceedRecords;
    /**
     * 读出成功的记录字节数
     */
    private long readSucceedBytes;
    /**
     * 读出失败的记录数
     */
    private long readFailedRecords;
    /**
     * 读出失败的记录字节数
     */
    private long readFailedBytes;
    /**
     * 写入成功的记录数
     */
    private long writeSucceedRecords;
    /**
     * 写入成功的记录字节数
     */
    private long writeSucceedBytes;
    /**
     * 写入失败的记录数
     */
    private long writeFailedRecords;
    /**
     * 写入失败的记录字节数
     */
    private long writeFailedBytes;

    /**
     * 任务失败错误信息
     */
    private String errorMsg;
}
