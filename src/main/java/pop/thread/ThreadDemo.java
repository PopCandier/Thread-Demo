package pop.thread;

/**
 * @program: thread
 * @description: 线程的几种创建方法
 * @author: Pop
 * @create: 2019-06-03 17:09
 **/
public class ThreadDemo extends Thread{
    @Override
    public void run() {
        System.out.println("通过继承 Thread 创建的线程");
    }

    public static void main(String[] args) {
        new ThreadDemo().start();//启动一个线程
    }
}
