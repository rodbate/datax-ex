package com.github.rodbate.datax.common.protocol.request;

import com.github.rodbate.datax.common.protocol.AbstractDeserializer;
import com.github.rodbate.datax.common.report.DataXJobReportInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * User: rodbate
 * Date: 2019/3/7
 * Time: 11:18
 */
@Getter
@Setter
public class DataXJobReportRequest extends AbstractDeserializer {
    private long jobId;
    private DataXJobReportInfo reportInfo;
}
