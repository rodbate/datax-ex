package com.github.rodbate.datax.server.web.controller;

import com.github.rodbate.datax.common.web.WebResponse;
import com.github.rodbate.datax.common.web.dto.resp.GetJobConfigResp;
import com.github.rodbate.datax.server.web.dto.req.StartJobReq;
import com.github.rodbate.datax.server.web.service.DataXServerApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:36
 */
@RestController
@RequestMapping(value = "/datax/server/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DataXServiceApiController {

    @Autowired
    private DataXServerApiService dataXServerApiService;


    @PostMapping(value = "/job/start")
    public Mono<WebResponse<Object>> startJob(@Valid @RequestBody Mono<StartJobReq> req) {
        return this.dataXServerApiService.startJob(req).then(Mono.just(WebResponse.buildSuccessResponse()));
    }


    @GetMapping(value = "/job/{jobId}/stop")
    public Mono<WebResponse<Object>> stopJob(@PathVariable("jobId") Long jobId) {
        return this.dataXServerApiService.stopJob(jobId).then(Mono.just(WebResponse.buildSuccessResponse()));
    }


    @GetMapping(value = "/job/{jobId}/config")
    public Mono<WebResponse<GetJobConfigResp>> getJobConfig(@PathVariable("jobId") Long jobId) {
        return this.dataXServerApiService.getJobConfig(jobId).map(WebResponse::buildSuccessResponse);
    }

}
