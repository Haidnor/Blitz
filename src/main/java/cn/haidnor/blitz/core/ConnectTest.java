package cn.haidnor.blitz.core;

import cn.haidnor.blitz.util.FileUtil;
import cn.haidnor.blitz.util.HttpUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 多线程下载多个文件
 * https://www.qsptv.ne
 *
 * @author Haidnor
 */
public class ConnectTest implements Runnable {

    public static int threadCount = 0;
    public static int fileNum;
    public static int connectSize;

    public void run() {
        int n = 0;
        synchronized (this) {
            fileNum += 1;
            n = fileNum;
            threadCount++;
        }

        // 下载 URL 根据不同的网站自行修改
        String address = "https://cn1.ruioushang.com/hls/20190218/e6a823fd631ed4b96faac86367f5e39e/1550432694/film_";
        String filename = FileUtil.supplementZero(5, n);
        address = address + filename + ".ts";
        String path = "D:/video/" + filename + ".ts";

        int connection = HttpUtil.testHttpConnection(address, 30000);

        synchronized (this) {
            if (connection == 200) {
                connectSize++;
            }
            threadCount--;
        }
    }

    public static void main(String[] args) {
        // 下载文件数
        int num = 2000;
        // 最大并发数
        int threadSize = 100;

        int i = 0;
        boolean mark = true;
        while (mark) {
            if (threadCount <= threadSize) {
                new Thread(new ConnectTest(), "Thread" + i).start();
                i++;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i >= num) {
                mark = false;
            }
        }

        System.out.println("可下载总量:" + connectSize);
    }


}