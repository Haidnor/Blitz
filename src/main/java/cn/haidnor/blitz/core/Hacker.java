package cn.haidnor.blitz.core;

import cn.haidnor.blitz.util.FileUtil;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 使用 cmd 指令合并文件例如
 * copy/b E:\video\*.ts E:\new.ts
 * https://www.qsptv.ne
 */
public class Hacker implements Runnable {
    public static int threadCount = 0;
    public static int folder;

    @Override
    public void run() {
        int x = 0;
        synchronized (new Object()) {
            folder = folder + 1;
            x = folder;
            threadCount++;
        }

        // 下载 URL 根据不同的网站自行修改
        String httpURL = "https://cn1.ruioushang.com/hls/20190218/e6a823fd631ed4b96faac86367f5e39e/1550432694/film_";
        String filename = FileUtil.supplementZero(5, x);
        httpURL = httpURL + filename +  ".ts";
        String path = "e:/video/" + filename + ".ts";


        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(httpURL);
            URLConnection connection = new URL(httpURL).openConnection();
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
            System.out.println(Thread.currentThread().getName() + " Complete：" + httpURL);

        } catch (Exception e) {
            System.out.println("Error:" + httpURL);
            // e.printStackTrace();
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
        while (true) {
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
                break;
            }
        }
    }

}