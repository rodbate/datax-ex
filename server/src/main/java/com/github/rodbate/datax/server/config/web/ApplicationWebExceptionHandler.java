package com.github.rodbate.datax.server.config.web;

import com.alibaba.fastjson.JSON;
import com.github.rodbate.datax.common.web.ReturnCode;
import com.github.rodbate.datax.common.web.WebResponse;
import com.github.rodbate.datax.server.exceptions.DataXServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:40
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ApplicationWebExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        WebResponse resp;
        if (ex instanceof DataXServerException) {
            DataXServerException serviceException = (DataXServerException) ex;
            log.error("encounter service exception", serviceException);
            String msg = serviceException.getReturnCode().getDefaultMsg();
            if (serviceException.getArgs() != null && serviceException.getArgs().length > 0) {
                msg = MessageFormat.format(msg, serviceException.getArgs());
            }
            resp = WebResponse.buildErrorResponse(serviceException.getReturnCode(), msg, ex);
        } else if (ex instanceof WebExchangeBindException) {
            log.error("encounter method argument not valid exception", ex);
            resp = WebResponse.buildErrorResponse(ReturnCode.BAD_REQUEST, ReturnCode.BAD_REQUEST.getDefaultMsg(), ex);
        } else {
            log.error("encounter unknown exception", ex);
            resp = WebResponse.buildErrorResponse(ReturnCode.INTERNAL_SERVER_ERROR, ReturnCode.INTERNAL_SERVER_ERROR.getDefaultMsg(), ex);
        }

        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response.writeWith(Mono.fromSupplier(() -> response.bufferFactory().wrap(JSON.toJSONBytes(resp))));
    }
}
