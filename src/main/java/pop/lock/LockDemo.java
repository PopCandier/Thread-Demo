package pop.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: thread
 * @description:
 * @author: 范凌轩
 * @create: 2019-06-26 16:25
 **/
public class LockDemo {

    private  static int count = 0;

    static Lock lock = new ReentrantLock();

    public static void inc(){

//        if(lock.tryLock()){
            lock.lock();
            //粒度更小
            try {

                Thread.sleep(1);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
            count++;
            lock.unlock();
//        }

    }

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i <1000 ; i++) {
            new Thread(()->{LockDemo.inc();}).start();
        }

        Thread.sleep(3000);
        System.out.println(count);

    }

}
