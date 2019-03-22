package com.github.rodbate.datax.common.protocol.request;

import com.github.rodbate.datax.common.protocol.AbstractDeserializer;
import lombok.Getter;
import lombok.Setter;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 9:26
 */
@Getter
@Setter
public class HeartbeatRequest extends AbstractDeserializer {
    private String clientId;
}
