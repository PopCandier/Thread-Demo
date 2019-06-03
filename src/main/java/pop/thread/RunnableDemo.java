package pop.thread;

/**
 * @program: thread
 * @description:
 * @author: Pop
 * @create: 2019-06-03 17:22
 **/
public class RunnableDemo implements Runnable{

    public void run() {
        System.out.println("实现Runnable 接口所创建");
    }

    public static void main(String[] args) {
        new Thread(new RunnableDemo()).start();
    }
}
