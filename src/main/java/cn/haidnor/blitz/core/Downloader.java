package cn.haidnor.blitz.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 单文件多线程下载
 *
 * @author Haidnor
 */
public class Downloader {
    /**
     * 目标地址
     */
    private URL url;
    /**
     * 本地文件
     */
    private File file;

    /**
     * 下载的总线程数
     */
    private static final int THREAD_AMOUNT = 8;

    /**
     * 下载目录。如果该目录不存在则会自动创建
     */
    private static final String DOWNLOAD_DIR_PATH = "E:/Download";

    /**
     * 每个线程下载多少字节
     */
    private int threadLen;

    /**
     * 通过构造函数传入下载地址
     */
    public Downloader(String address, String fileName) throws IOException {
        url = new URL(address);

        // 自动创建文件目录
        File dir = new File(DOWNLOAD_DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 创建下载的文件
        file = new File(dir, fileName);
    }

    public void download() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        // 获取文件长度
        int totalLen = conn.getContentLength();
        // 计算每个线程要下载的长度
        threadLen = (totalLen + THREAD_AMOUNT - 1) / THREAD_AMOUNT;

        System.out.println("totalLen=" + totalLen / 1048576.0 + ",threadLen:" + threadLen / 1048576.0);

        // 在本地创建一个和服务端大小相同的文件
        RandomAccessFile raf = new RandomAccessFile(file, "rws");
        // 设置文件的大小
        raf.setLength(totalLen);
        raf.close();

        // 开启3条线程, 每个线程下载一部分数据到本地文件中
        for (int i = 0; i < THREAD_AMOUNT; i++) {
            new DownloadThread(i).start();
        }

    }

    private class DownloadThread extends Thread {
        private int id;

        public DownloadThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            // 起始位置
            int start = id * threadLen;
            // 结束位置
            int end = id * threadLen + threadLen - 1;
            System.out.println("线程" + id + ": " + start + "-" + end);
            RandomAccessFile raf = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);

                // 设置浏览器标识
                conn.setRequestProperty("User-agent", "Mozilla/4.0");
                // 设置当前线程下载的范围
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);

                InputStream in = conn.getInputStream();
                raf = new RandomAccessFile(file, "rws");
                // 设置保存数据的位置
                raf.seek(start);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    raf.write(buffer, 0, len);
                }

                System.out.println("线程" + id + "下载完毕");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // 下载链接
        String address = "http://forspeed.onlinedown.net/down/eclipse-jee-mars-1-win32-x86_64.zip";
        new Downloader(address, "eclipse-jee-mars-1-win32-x86_64.zip").download();
    }

}