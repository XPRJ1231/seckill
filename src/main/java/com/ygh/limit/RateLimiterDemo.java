package com.ygh.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class RateLimiterDemo {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(2); //速率限制器

        List<Runnable> tasks = new ArrayList<Runnable>();
        for(int i = 0;i < 10; i++){
            tasks.add(new UserRequest(i));
        }
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (Runnable runnable : tasks){
            System.out.println("等待时间：" + rateLimiter.acquire());
            threadPool.execute(runnable);
        }


    }

    private static class UserRequest implements Runnable {
        private int id;

        public UserRequest(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println(id);
        }
    }
}

/*
public class RateLimiterDemo {
    private final Semaphore semaphore;
    private final int permitsPerSecond;

    public RateLimiterDemo(int permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);
    }

    public long acquire() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        semaphore.acquire();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public void release() {
        semaphore.release();
    }

    private static class UserRequest implements Runnable {
        private final int id;

        public UserRequest(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println(id);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiterDemo rateLimiter = new RateLimiterDemo(2);

        List<Runnable> tasks = new ArrayList<Runnable>();
        for(int i = 0;i < 10; i++){
            tasks.add(new UserRequest(i));
        }
        ExecutorService threadPool = Executors.newCachedThreadPool();

        for(Runnable runnable : tasks) {
            try {
                System.out.println("等待时间：" + rateLimiter.acquire());
                threadPool.execute(runnable);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                rateLimiter.release();
            }
        }
    }
}
*/
