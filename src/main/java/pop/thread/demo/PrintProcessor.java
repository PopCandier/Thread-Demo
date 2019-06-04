package pop.thread.demo;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: thread
 * @description: 一个实现
 * @author: Pop
 * @create: 2019-06-04 10:55
 **/
public class PrintProcessor extends Thread implements  RequestProcessor{

    //定义一个阻塞队列
    LinkedBlockingQueue<Request> requests =
            new LinkedBlockingQueue<Request>();

    //我们将定义下一个请求，构成责任链
    private final RequestProcessor next;

    public PrintProcessor(RequestProcessor next) {
        this.next = next;
    }

    @Override
    public void run() {
        while(true){

            try {
                //如果阻塞队列没有可取出的request，将会被阻塞
                Request request = requests.take();
                System.out.println("print Data:"+request.getName());
                //调用下一条请求
                next.processRequest(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void processRequest(Request request) {
            requests.add(request);//将请求添加到阻塞队列
    }
}
