package cn.haidnor.blitz.pojo;

/**
 * 封装请求相关信息
 *
 * @author Haidnor
 */
public class HttpRequest {
    /**
     * 请求地址
     */
    public String address;

    /**
     * 文件名
     */
    public String filename;

    /**
     * 连接次数
     */
    public int count = 0;
}
