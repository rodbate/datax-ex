package com.github.rodbate.datax.common.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * User: rodbate
 * Date: 2019/3/6
 * Time: 11:38
 */
public final class TransportUtil {


    public static String buildClientId(int port) {
        InetAddress address = InetUtil.getLocalhostLanAddress();
        return address.getHostAddress() + ":" + port;
    }

    public static String buildClientId(InetSocketAddress socketAddress) {
        return socketAddress.getHostString() + ":" + socketAddress.getPort();
    }

    private TransportUtil() { }
}
