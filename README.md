# Thread-Demo

#### ReentrantLock 的实现原理

首先了解到sychronized内也包含了三种锁的，偏向锁，轻量级锁，重量级锁。

锁的目的在于在某个时间段，让并行的程序变为串行，来达到原子性和后面读写操作的可见性问题。之前分析了sychronized内部维护了同步队列和唤醒的机智，那么Lock中是否也有这样的规则呢。

#### AQS

答案是肯定的，Lock中存在一个同步队列AQL，全名AbstractQueueSynchronizer，这是一个核心的组件，可以将获得锁失败的线程封装成一个Node，最后变成一个FIFO双向链表，可访问前节点也可以访问后节点，这之后并将其挂起，等待下一次唤醒。

#### AQS的两种功能

aqs的两种功能：独占和共享

* 独占锁，每次只能有一个线程持有锁，ReentrantLock就是以独占方式实现的互斥锁

* 共享锁，允许多个线程同时获得锁，并发访问共享数据，比如

  ReentrantReadWriteLock中的读锁。

AQS中的双向链表，会将失败的线程包装成Node，加入到AQS队列中。

```java
static final class Node {
       
        static final Node SHARED = new Node();
        
        static final Node EXCLUSIVE = null;

        static final int CANCELLED =  1;
        
        static final int SIGNAL    = -1;
        
        static final int CONDITION = -2;
       
        static final int PROPAGATE = -3;

        
        volatile int waitStatus;

        
        volatile Node prev;

        volatile Node next;

        volatile Thread thread;

        Node nextWaiter;

        
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {   
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
```

首先，添加节点的时候，尾部节点的指向会发生变化，这里会设计两个变化。

* 新的线程封装成Node节点，追加到同步队列中，设置prev节点以及修改当前节点的前置节点指向自己，组成新的链条
* 通过CAS将tail重新指向新的尾部节点。

![1561973693680](C:\Users\范凌轩\AppData\Roaming\Typora\typora-user-images\1561973693680.png)

首先呢，head节点表示已经获得锁的节点，当头节点释放同步状态的时候，会唤醒后续节点获得锁，比唤醒的锁将会把自己设置为节点。

![1561973774512](C:\Users\范凌轩\AppData\Roaming\Typora\typora-user-images\1561973774512.png)

* 修改head节点，指向下一个获得锁的节点
* 新获得锁的节点，将prev的指向null，并且之前离开队列的节点的next也为null

同时，设置head的时候，不需要cas来保证，因为head节点是由获得锁的节点来承担的，而同步锁只能由一个线程获得，所以不需要cas保证，只需要把head节点，设置为原首节点的后续节点，并且断开原head节点的next引用即可。

#### ReentrantLock的源码

AQS作为一个同步工具，在juc中用到的地方非常多。

![1561976019636](C:\Users\范凌轩\AppData\Roaming\Typora\typora-user-images\1561976019636.png)

所谓的Lock，其实调用的是底层Sync的Lock

方法，Sync作为一个抽象静态内部类，他继承了AQS来实现

重入锁功能。

同时Sync有两个具体的实现类，分别是

* NofairSync：表示可以存在抢占所的功能，这里所谓的不公平，表示有新线程来的时候，他们都有机会“插队”获得新的锁。
* FailSync：表示所有的线程严格按照FIFO来获得锁。

