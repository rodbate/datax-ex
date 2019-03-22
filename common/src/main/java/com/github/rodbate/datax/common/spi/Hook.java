package com.github.rodbate.datax.common.spi;

import com.github.rodbate.datax.common.util.Configuration;

import java.util.Map;

/**
 * Created by xiafei.qiuxf on 14/12/17.
 */
public interface Hook {

    /**
     * 返回名字
     *
     * @return
     */
    String getName();

    /**
     * TODO 文档
     *
     * @param jobConf
     * @param msg
     */
    void invoke(Configuration jobConf, Map<String, Number> msg);

}
