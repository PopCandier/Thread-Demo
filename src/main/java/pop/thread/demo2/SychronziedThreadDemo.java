package pop.thread.demo2;

/**
 * @author Pop
 * @date 2019/6/4 21:20
 */
public class SychronziedThreadDemo {

    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        for(int i =0;i<1000;i++){

            new Thread(()->{
                synchronized(SychronziedThreadDemo.class){
                    try {
                        Thread.sleep(2);//让他改变的时候多延迟一下
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count ++; // 1000 个线程将会对这个count进行数值修改
                }
            }).start();

        }
        Thread.sleep(3000);
        System.out.println(count);
    }

}
