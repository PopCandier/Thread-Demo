package pop.thread.demo1;

/**
 * @program: thread
 * @description: 食物
 * @author: 范凌轩
 * @create: 2019-06-04 12:23
 **/
public class Food {
    private String name;

    public Food(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Food{" +
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
