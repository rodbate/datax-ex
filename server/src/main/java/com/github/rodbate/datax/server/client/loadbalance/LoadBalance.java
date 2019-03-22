package com.github.rodbate.datax.server.client.loadbalance;

import java.util.List;

/**
 *
 *
 * User: rodbate
 * Date: 2019/3/6
 * Time: 14:22
 */
public interface LoadBalance<T> {

    T select(List<T> list);

}
