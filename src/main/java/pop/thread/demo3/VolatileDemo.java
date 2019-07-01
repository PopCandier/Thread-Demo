package pop.thread.demo3;

/**
 * @author Pop
 * @date 2019/6/18 22:48
 */
public class VolatileDemo {

    public volatile  static boolean volatile_stop = false;
    public static boolean unvolatile_stop=false;
    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(()->{
            int i =0;
            while(!volatile_stop){
                i++;
            }
        });
        thread.start();
        Thread.sleep(1000);
        volatile_stop=true;
    }

}
