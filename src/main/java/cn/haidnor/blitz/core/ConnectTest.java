package cn.haidnor.blitz.core;

import cn.haidnor.blitz.util.FileUtil;
import cn.haidnor.blitz.util.HttpUtil;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程下载多个文件
 * https://www.qsptv.ne
 *
 * @author Haidnor
 */
public class ConnectTest implements Runnable {
    static int size;
    static ArrayDeque<HttpRequest> queue;

    public void run() {
        HttpRequest request;

        Object obj = new Object();

        synchronized (obj) {
            request = queue.pop();
        }

        int connection = HttpUtil.testHttpConnection(request.address, 3000);
        System.out.println(request.address + " :" + connection);

        synchronized (obj) {
            if (connection == 200) {
                size++;
                System.out.println("可用连接数:" + size);
            } else {
                // 连接测试次数
                if (request.count < 5) {
                    queue.add(request);
                }
                System.out.println("可用连接数:" + size);
                request.count++;
            }
        }
    }

    public static void main(String[] args) {
        // 测试总数
        int totalFiles = 2000;


        queue = new ArrayDeque<HttpRequest>();
        for (int i = 1; i <= totalFiles; i++) {
            // 请求地址.去除文件编号以及后缀
            String address = "https://cn1.ruioushang.com/hls/20190218/e6a823fd631ed4b96faac86367f5e39e/1550432694/film_";
            String filename = FileUtil.supplementZero(5, i);
            address = address + filename + ".ts";

            HttpRequest request = new HttpRequest();
            request.address = address;
            request.filename = filename;
            request.count = 0;

            queue.add(request);
        }

        // TreadPool
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);

        while (!queue.isEmpty()) {
            Runnable thread = new ConnectTest();
            fixedThreadPool.submit(thread);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fixedThreadPool.shutdown();
    }

}

class HttpRequest {
    /**
     * 请求地址
     */
    String address;

    /**
     * 文件名
     */
    String filename;

    /**
     * 连接次数
     */
    int count = 0;
}