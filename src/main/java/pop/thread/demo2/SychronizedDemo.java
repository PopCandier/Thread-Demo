package pop.thread.demo2;

/**
 * @author Pop
 * @date 2019/6/4 21:47
 */
public class SychronizedDemo {

    public synchronized void demo(){}

    public static synchronized  void demo2(){}

    public void demo3(){

        synchronized (this){
            // todo
        }

    }

    public void demo4(){
        synchronized (SychronizedDemo.class){
            // todo
        }
    }
}
