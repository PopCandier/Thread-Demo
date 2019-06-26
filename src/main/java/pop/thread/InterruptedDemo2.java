package pop.thread;

/**
 * @program: thread
 * @description:
 * @author: 范凌轩
 * @create: 2019-06-04 17:43
 **/
public class InterruptedDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread  = new Thread(()->{
            while(true){

                if(Thread.currentThread().isInterrupted()){//如果标识已经改变成了中断

                    System.out.println("before "+Thread.currentThread().isInterrupted());

                    boolean interrupted=Thread.interrupted();//复位，将标识改成false
                    System.out.println("返回的标志: "+interrupted);

                    System.out.println("after "+Thread.currentThread().isInterrupted());
                }

            }
        });
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }
}
