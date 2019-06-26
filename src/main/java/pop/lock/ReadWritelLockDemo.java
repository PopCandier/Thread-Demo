package pop.lock;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @program: thread
 * @description:
 * @author: 范凌轩
 * @create: 2019-06-26 16:44
 **/
public class ReadWritelLockDemo {
    /*
    * 在读多写少的情况下，重入读写锁性能更高
    * 他将维护一对锁，一个读锁一个写锁
    * 读锁和读锁不互斥，但是读写锁就会互斥，排他，写写锁更是
    当读比写多的场景，这个会更好点
    * */

    static Map<String,Object> cacheMap = new HashMap<>();
    static ReentrantReadWriteLock readWriteLock = new
            ReentrantReadWriteLock();

    static Lock read = readWriteLock.readLock();
    static Lock write = readWriteLock.writeLock();

    public static final Object get(String key){

        System.out.println("开始读取数据");

        read.lock();//读锁
        try {
            return  cacheMap.get(key);
        }finally {
            read.unlock();
        }
        /**
         * 在读操作开始的时候，会获得读锁，而并发读的时候，读锁
         * 并不会阻塞，因为读操作不会影响执行结果。
         */
    }

    public static final Object put(String key, Object value){

        write.lock();

        System.out.println("开始写数据");
        try{
            return cacheMap.put(key,value);
        }finally {
            write.unlock();
        }
        /**
         * 写锁
         * 执行写操作的时候，线程必须获得写锁，当线程已经有持有
         * 锁的情况下的时候，当前线程将会阻塞，只有释放后，其它写操作
         * 才会进行
         */
    }
    /**
     * 读写锁保证了读锁的并发性
     * 同时也保证了每次操作对之后的所有读写操作可见。
     */


}
