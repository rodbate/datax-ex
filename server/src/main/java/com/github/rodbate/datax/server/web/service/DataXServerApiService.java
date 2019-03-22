package com.github.rodbate.datax.server.web.service;

import com.github.rodbate.datax.common.web.dto.resp.GetJobConfigResp;
import com.github.rodbate.datax.server.web.dto.req.StartJobReq;
import reactor.core.publisher.Mono;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:46
 */
public interface DataXServerApiService {


    Mono<Void> startJob(Mono<StartJobReq> req);


    Mono<Void> stopJob(Long jobId);


    Mono<GetJobConfigResp> getJobConfig(Long jobId);

}
