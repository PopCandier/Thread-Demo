package pop.thread;

/**
 * @program: thread
 * @description:
 * @author: 范凌轩
 * @create: 2019-06-04 17:17
 **/
public class InterruptedDemo1 extends Thread{

    public static void main(String[] args) throws InterruptedException {
        InterruptedDemo1 thread = new InterruptedDemo1();
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(1000);
                System.out.println("有标志");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
