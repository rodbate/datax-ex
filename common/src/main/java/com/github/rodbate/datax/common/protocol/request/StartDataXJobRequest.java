package com.github.rodbate.datax.common.protocol.request;

import com.github.rodbate.datax.common.protocol.AbstractDeserializer;
import lombok.Getter;
import lombok.Setter;

/**
 * User: rodbate
 * Date: 2019/3/5
 * Time: 15:38
 */
@Getter
@Setter
public class StartDataXJobRequest extends AbstractDeserializer {
    private long jobId;
    private String jobConfUrl;
}
