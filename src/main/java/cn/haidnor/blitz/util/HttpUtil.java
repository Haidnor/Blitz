package cn.haidnor.blitz.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http 连接相关工具
 *
 * @author Haidnor
 */
public class HttpUtil {

    /**
     * 测试 Http url 连接
     *
     * @param address http url
     * @param timeout 设置响应超时时间
     * @return http 响应码
     */
    public static int testHttpConnection(String address, int timeout) {
        int status = 404;
        try {
            URL url = new URL(address);
            HttpURLConnection oc = (HttpURLConnection) url.openConnection();
            oc.setUseCaches(false);
            // set timeout
            oc.setConnectTimeout(timeout);
            // request status
            status = oc.getResponseCode();
        } catch (IOException e) {
            return status;
        }
        return status;
    }

}
