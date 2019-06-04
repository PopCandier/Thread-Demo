package pop.thread.demo;


/**
 * @program: thread
 * @description:
 * @author: Pop
 * @create: 2019-06-04 11:06
 **/
public class Boostrap {

    PrintProcessor printProcessor;

    protected Boostrap(){
        SaveProcesspr saveProcesspr = new SaveProcesspr();
        saveProcesspr.start();//启动的时候，由于阻塞队列没有request请求，该线程将会被阻塞
        printProcessor = new PrintProcessor(saveProcesspr);
        printProcessor.start();
        //printProcessor将saveProcessper作为下一个节点
        //也同样，当前阻塞队列中没有数据，将会被阻塞
    }

    private  void addRequest(Request request){
        printProcessor.processRequest(request);
    };

    public static void main(String[] args) {
        Request request = new Request();
        request.setName("Pop");
        new Boostrap().addRequest(request);
    }



}
