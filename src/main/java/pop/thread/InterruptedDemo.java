package pop.thread;

import java.util.Date;

/**
 * @program: thread
 * @description:
 * @author: 范凌轩
 * @create: 2019-06-04 17:17
 **/
public class InterruptedDemo extends Thread{

    public static void main(String[] args) throws InterruptedException {
        InterruptedDemo thread = new InterruptedDemo();
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                System.out.println("没有标志");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
