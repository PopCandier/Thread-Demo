package pop.thread;

import java.util.concurrent.*;

/**
 * @program: thread
 * @description:
 * @author: Pop
 * @create: 2019-06-03 17:54
 **/
public class CallableDemo implements Callable<String> {

    public String call() throws Exception {
        return String.valueOf(1+1);//简单计算一下1+1
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建核心线程数与最大线程数为1个的线程池
        ExecutorService executorService
                 = Executors.newFixedThreadPool(1);
        CallableDemo callableDemo = new CallableDemo();
        //活动返回任务
        Future<String> future = executorService.submit(callableDemo);
        //打印出结果
        System.out.println(future.get());//在未得到数据之前阻塞
        executorService.shutdown();//销毁线程池中的线程

    }
}
