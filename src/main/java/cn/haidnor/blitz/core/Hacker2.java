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
public class Hacker2 implements Runnable {
    static int size;
    static ArrayDeque<HttpRequest> queue;

    public void run() {
        HttpRequest request;
        Object obj = new Object();
        synchronized (obj) {
            request = queue.pop();
        }

        String path = "E:/video/" + request.filename + ".ts";


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
            synchronized (new Object()) {
                size++;
                System.out.println(Thread.currentThread().getName() + " Complete：" + request.address + "   :" + size);
            }
        } catch (Exception e) {
            synchronized (new Object()) {
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
        // 测试总数
        int totalFiles = 1944;

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