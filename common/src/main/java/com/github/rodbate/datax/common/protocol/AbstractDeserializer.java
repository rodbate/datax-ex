package com.github.rodbate.datax.common.protocol;

import com.alibaba.fastjson.JSON;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 9:12
 */
public abstract class AbstractDeserializer {


    public static <T> T decode(byte[] body, Class<T> clazz) {
        return JSON.parseObject(body, clazz);
    }

    public static byte[] encode(Object obj) {
        return JSON.toJSONBytes(obj);
    }

}
