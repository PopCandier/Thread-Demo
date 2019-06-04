package pop.thread.demo1;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: thread
 * @description: 服务员
 * @author: 范凌轩
 * @create: 2019-06-04 12:25
 **/
public class Waiter extends Thread{

    private LinkedBlockingQueue<Food> queue;

    public Waiter(LinkedBlockingQueue<Food> queue,String name) {
        super(name);
        this.queue = queue;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                Food food;
                if((food=queue.poll())==null?false:true){
                    System.out.println(Thread.currentThread().getName()+String.format("获得菜：%s",food));
                }else{
                    System.out.println(Thread.currentThread().getName()+" 没有获得菜");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
