package com.github.rodbate.datax.server.web.dto.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:58
 */
@Getter
@Setter
public class StartJobReq {
    @NotNull
    private Long jobId;
    @NotBlank
    private String jobConf;
}
