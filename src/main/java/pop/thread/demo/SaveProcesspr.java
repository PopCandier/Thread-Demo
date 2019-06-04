package pop.thread.demo;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: thread
 * @description: 保存处理
 * @author: Pop
 * @create: 2019-06-04 11:02
 **/
public class SaveProcesspr extends Thread implements RequestProcessor{

    LinkedBlockingQueue<Request> requests = new LinkedBlockingQueue<Request>();

    @Override
    public void run() {
        while(true){

            try {
                //如果阻塞队列没有可取出的request，将会被阻塞
                Request request = requests.take();
                System.out.println("save Data:"+request.getName());
                //调用下一条请求
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void processRequest(Request request) {
            requests.add(request);
    }
}
