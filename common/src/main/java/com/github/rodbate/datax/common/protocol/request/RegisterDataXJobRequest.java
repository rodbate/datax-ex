package com.github.rodbate.datax.common.protocol.request;

import com.github.rodbate.datax.common.protocol.AbstractDeserializer;
import lombok.Getter;
import lombok.Setter;

/**
 * User: rodbate
 * Date: 2019/3/7
 * Time: 11:15
 */
@Getter
@Setter
public class RegisterDataXJobRequest extends AbstractDeserializer {
    private long jobId;
}
