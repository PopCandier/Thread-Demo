package pop.thread.demo;

/**
 * @program: thread
 * @description: 被定义用来模拟请求的
 * 对象
 * @author: Pop
 * @create: 2019-06-04 10:52
 **/
public class Request {
    private String name;

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

