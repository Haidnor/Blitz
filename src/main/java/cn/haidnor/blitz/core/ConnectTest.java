package cn.haidnor.blitz.core;

import cn.haidnor.blitz.bean.HttpRequest;
import cn.haidnor.blitz.util.FileUtil;
import cn.haidnor.blitz.util.HttpUtil;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试 url 连接
 *
 * @author Haidnor
 */
public class ConnectTest implements Runnable {

    static int size;
    static ArrayDeque<HttpRequest> queue;

    public void run() {
        HttpRequest request;

        synchronized (ConnectTest.class) {
            request = queue.pop();
        }

        int connection = HttpUtil.testHttpConnection(request.address, 3000);
        System.out.println(request.address + " :" + connection);

        synchronized (ConnectTest.class) {
            if (connection == 200) {
                size++;
                System.out.println("Available connection count:" + size);
            } else {
                // 连接测试次数
                if (request.count < 100) {
                    queue.add(request);
                }
                System.out.println("Available connection count:" + size);
                request.count++;
            }
        }
    }

    public static void main(String[] args) {
        // 测试总数
        int totalFiles = 999;

        // 起始数字
        int start = 1;

        queue = new ArrayDeque<HttpRequest>();
        for (int i = start; i <= totalFiles; i++) {
            // 请求地址.去除文件编号以及后缀
            String address = "https://haoa.haozuida.com/20180412/3McKJ7uM/800kb/hls/pWOhZ4a2573";
            String filename = FileUtil.supplementZero(3, i);
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
