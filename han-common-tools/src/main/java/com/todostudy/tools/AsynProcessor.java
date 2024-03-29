package com.todostudy.tools;

import java.util.concurrent.*;

/**
 * @author hanson
 * hwo to use: 多个提交并行执行
 * Callable<String> task=()->{
 *             return "ss";
 *         };
 *         Future future = submit(task);
 *         future.get();
 */
public class AsynProcessor {
    private static ExecutorService exec = new ThreadPoolExecutor(2, 4, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(200),
            new ThreadPoolExecutor.CallerRunsPolicy());
    public static void execute(Runnable command) {
        exec.execute(command);
    }
    /**
     * 子线程执行结束future.get()返回null，若没有执行完毕，主线程将会阻塞等待
     * @param command
     * @return
     */
    public static Future submit(Runnable command) {
        return exec.submit(command);
    }
    /**
     * 子线程中的返回值可以从返回的future中获取：future.get();
     * @param command
     * @return
     */
    public static Future submit(Callable command) {
        return exec.submit(command);
    }
    public static void shutdown(){
        exec.shutdown();
    }

    /*public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<String> task=()->{
            return "ss";
        };
        Future future = submit(task);
        System.out.println(future.get());
    }*/

}
