package cn.haidnor.blitz.core;

import cn.haidnor.blitz.pojo.HttpRequest;
import cn.haidnor.blitz.util.FileUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程下载多个文件
 * https://www.qsptv.ne
 *
 * @author Haidnor
 */
public class Hacker implements Runnable {
    static int size;
    static ArrayDeque<HttpRequest> queue;

    public void run() {
        HttpRequest request = null;
        synchronized (Hacker.class) {
            request = queue.pop();
        }

        String path = "d:/test/" + request.filename + ".ts";

        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            URLConnection urlConnection = new URL(request.address).openConnection();
            urlConnection.setRequestProperty("User-agent", "Mozilla/5.0");

            InputStream inputStream = urlConnection.getInputStream();

            dataInputStream = new DataInputStream(inputStream);
            fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());

            synchronized (Hacker.class) {
                size++;
                System.out.println(Thread.currentThread().getName() + " Complete：" + request.address + "   :" + size);
            }

        } catch (Exception e) {
            synchronized (Hacker.class) {
                // 连接测试次数
                if (request.count < 10) {
                    queue.add(request);
                }
                request.count++;
            }
        } finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // 测试总数  钢铁侠3 1600
        int totalFiles = 1600;

        // 起始数字
        int start = 1;

        queue = new ArrayDeque<HttpRequest>();
        for (int i = start; i <= totalFiles; i++) {
            // 请求地址.去除文件编号以及后缀
            String address = " https://cn4.ruioushang.com/hls/20181203/e766ce3e55f1b3a1361df734cb637d52/1543814519/film_";
            String filename = FileUtil.supplementZero(5, i);
            address = address + filename + ".ts";

            HttpRequest request = new HttpRequest();
            request.address = address;
            request.filename = filename;
            request.count = 0;

            queue.add(request);
        }

        // TreadPool
        // 线程数与本地的网络有关,根据情况设置
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

        while (!queue.isEmpty()) {
            Runnable thread = new Hacker();
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