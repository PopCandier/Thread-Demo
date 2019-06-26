package pop.thread;

import java.util.concurrent.TimeUnit;

/**
 * @program: thread
 * @description:
 * @author: 范凌轩
 * @create: 2019-06-04 17:55
 **/
public class InterruptedDemo3 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread  = new Thread(()->{
            for(;;){
                if(!Thread.currentThread().isInterrupted()){
                    try {
                        TimeUnit.SECONDS.sleep(2);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("异常抛出后"+Thread.currentThread().isInterrupted());
                    }
                }
            }

        });
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }
}
