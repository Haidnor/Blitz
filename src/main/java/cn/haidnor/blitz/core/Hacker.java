package cn.haidnor.blitz.core;

import cn.haidnor.blitz.util.FileUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 多线程下载多个文件
 * https://www.qsptv.ne
 *
 * @author Haidnor
 */
public class Hacker implements Runnable {
    public static int threadCount = 0;
    public static int fileNum;

    public void run() {
        int n = 0;
        synchronized (new Object()) {
            fileNum = fileNum + 1;
            n = fileNum;
            threadCount++;
        }

        // 下载 URL 根据不同的网站自行修改
        String url = "https://cn1.ruioushang.com/hls/20190218/e6a823fd631ed4b96faac86367f5e39e/1550432694/film_";
        String filename = FileUtil.supplementZero(5, n);
        url = url + filename + ".ts";
        String path = "e:/video/" + filename + ".ts";

        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");
            InputStream inputStream = connection.getInputStream();

            dataInputStream = new DataInputStream(inputStream);
            fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            System.out.println(Thread.currentThread().getName() + " Complete：" + url);

        } catch (Exception e) {
            System.out.println("Error:" + url);
            e.printStackTrace();
        }
        synchronized (new Object()) {
            threadCount--;
            // System.out.println("Current thread num:" + threadCount);
        }
    }

    public static void main(String[] args) {
        // 下载文件数
        int num = 2000;
        // 最大并发数
        int threadSize = 50;

        int i = 0;
        boolean mark = true;
        while (mark) {
            if (threadCount <= threadSize) {
                new Thread(new Hacker(), "Thread" + i).start();
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
    }

}