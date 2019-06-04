package pop.thread.demo1;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: thread
 * @description: 厨师
 * @author: 范凌轩
 * @create: 2019-06-04 12:26
 **/
public class Cook extends Thread{

    private LinkedBlockingQueue<Food> queue;

    public Cook(LinkedBlockingQueue<Food> queue,String name) {
        super(name);
        this.queue = queue;
    }

    private Food startCook(){
        return new Food("鱼香肉丝");
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(3000);//模拟做菜时间
                queue.add(startCook());
                System.out.println(Thread.currentThread().getName()+" 完成一盘鱼香肉丝");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
