package cn.haidnor.blitz.core;

import cn.haidnor.blitz.util.FileUtil;
import cn.haidnor.blitz.util.HttpUtil;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程下载多个文件
 * https://www.qsptv.ne
 *
 * @author Haidnor
 */
public class ConnectTest implements Runnable {
    public static int size;
    public static ArrayDeque<String> queue;

    public void run() {
        String address;

        synchronized (this) {
            address = queue.pop();
        }

        int connection = HttpUtil.testHttpConnection(address, 3000);
        System.out.println(address + "    :" + connection);

        synchronized (this) {
            if (connection == 200) {
                ++size;
                System.out.println("已完成:" + size);
            } else {
                queue.add(address);
            }
        }
    }

    public static void main(String[] args) {
        queue = new ArrayDeque<String>();

        for (int i = 0; i <= 1000; i++) {
            String address = "https://jingdian.qincai-zuida.com/20200610/8212_9426d3e9/1000k/hls/5e1ca694042";
            String filename = FileUtil.supplementZero(3, i);
            address = address + filename + ".ts";
            queue.add(address);
        }

        /*
         * 创建具一个可重用的，有固定数量的线程池
         * 每次提交一个任务就提交一个线程，直到线程达到线城池大小，就不会创建新线程了
         * 线程池的大小达到最大后达到稳定不变，如果一个线程异常终止，则会创建新的线程
         */
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);

        while (!queue.isEmpty()) {
            Runnable thread = new ConnectTest();
            fixedThreadPool.submit(thread);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        fixedThreadPool.shutdown();
    }

}