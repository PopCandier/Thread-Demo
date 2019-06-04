package pop.thread.demo1;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: thread
 * @description: 启动类
 * @author: 范凌轩
 * @create: 2019-06-04 12:23
 **/
public class BootStrap {
    /**
     * 3个厨师和2个服务员 ，厨师每隔3分钟烧一道菜 ，服务员每隔1分钟查看是否有菜做好，没有的话 ，就继续等待1分钟再次查看
     * 用java多线程的方式写出当前的代码展示
     */

    //放菜的台子
    private static LinkedBlockingQueue<Food> desk = new LinkedBlockingQueue<Food>();

    public static void main(String[] args) {
        for(int i =0;i<3;i++){//三个厨师
            new Cook(desk,"厨师 "+i).start();
        }
        for(int i =0;i<2;i++){//两个服务员
            new Waiter(desk,"服务员 "+i).start();
        }
    }
}
