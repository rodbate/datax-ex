package com.github.rodbate.datax.server.client.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:29
 */
public class RoundRobinAgentLoadBalance implements LoadBalance<String> {

    private final AtomicInteger idx = new AtomicInteger();

    @Override
    public String select(List<String> clients) {
        if (clients == null || clients.size() == 0) {
            return null;
        }
        int selectIdx = Math.abs(idx.incrementAndGet()) % clients.size();
        return clients.get(selectIdx);
    }

}
