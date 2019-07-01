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

![1561973693680](https://github.com/PopCandier/Thread-Demo/blob/master/images/1561973693680.png)

首先呢，head节点表示已经获得锁的节点，当头节点释放同步状态的时候，会唤醒后续节点获得锁，比唤醒的锁将会把自己设置为节点。

![1561973774512](https://github.com/PopCandier/Thread-Demo/blob/master/images/1561973774512.png)

* 修改head节点，指向下一个获得锁的节点
* 新获得锁的节点，将prev的指向null，并且之前离开队列的节点的next也为null

同时，设置head的时候，不需要cas来保证，因为head节点是由获得锁的节点来承担的，而同步锁只能由一个线程获得，所以不需要cas保证，只需要把head节点，设置为原首节点的后续节点，并且断开原head节点的next引用即可。

#### ReentrantLock的源码

AQS作为一个同步工具，在juc中用到的地方非常多。

![1561976019636](https://github.com/PopCandier/Thread-Demo/blob/master/images/1561976019636.png)

所谓的Lock，其实调用的是底层Sync的Lock

方法，Sync作为一个抽象静态内部类，他继承了AQS来实现

重入锁功能。

同时Sync有两个具体的实现类，分别是

* NofairSync：表示可以存在抢占所的功能，这里所谓的不公平，表示有新线程来的时候，他们都有机会“插队”获得新的锁。
* FailSync：表示所有的线程严格按照FIFO来获得锁。

下面来展示一下非公平锁和公平锁在代码上的区别。

对于非公平锁而言

```java
final void lock() {
    //新节点都会尝试去获得锁，如果获得就会直接成为head节点，否则就会进入等待队列
    //当然，这对已经入队列等待获得锁的node是不公平的
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

```

对于公平锁而言

```java
 final void lock() {
            acquire(1);
        }
```

很明显，公平锁再次抢占的实现，必须严格按照队列的抢夺顺序。

##### ReentrantLock 中的 State

在JUC中，很多锁中的State表达的含义是不一样的。

而在重入锁，他标识一个同步的状态，他包含两个含义。

* state = 0 时，表示无锁状态
* state > 0 时，表示已经有线程获得锁，也就是state=1，但是因为ReentrantLock允许重入，所以，同一个线程多次获得同步锁的时候，state会递增，同时也就意味着他需要释放与重入次数相同的锁才可以。
  * 例如重入5次，state =5，那么在释放锁的时候，同样需要释放5次知道stat=0的时候，其它线程才有资格获得锁。

##### 继续

接着来到AQS中的 acquire方法

```java
 public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```

这个方法的主要逻辑

* 通过 tryAcquire 尝试获取独占锁，如果成功返回 ture，失败返回false，那么对于这个方法中，只有失败才会尝试把自己变成node加入到队列中，并且挂起自己。

* 如果tryAcquire失败，则通过addWaiter 方法将当前线程封装成Node添加到AQS队列

  队列尾部。

* acquireQueued，将Node作为参数，通过自旋去尝试获取锁。

这里是非公平锁的例子

```java
protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
        
final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {//如果当前是无锁状态，尝试获得锁
                if (compareAndSetState(0, acquires)) {
                    //尝试cas操作，如果成功，就将当前独占线程设置为自己
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }//如果已经有线程获得了锁，并且发现自己就是获得锁的线程，增加重入次数
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
```

当然如果你获得锁失败了

```java
private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;//由于是第一次，所以头尾都是空的
        if (pred != null) {//这里就不会进入
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);//这里就是第一次直接进入的地方
        return node;
    }
```

enq方法就是通过自选的方式将当前节点添加到队列中

```java
private Node enq(final Node node) {
        for (;;) {
            Node t = tail;//同样，第一次进入，尾巴还是空
            if (t == null) { 
                //第一次是一定会进入这个方法中的，他初始化了头和尾节点
                //这个时候这个链表，head 和 tail指向的都是一个
                //new Node 节点，也就是意味着是一个完全空的节点
                if (compareAndSetHead(new Node()))
                    tail = head;//并且这个时候头尾相同，由于这个是一个死循环
                //方法，所以这个循环将会持续下去
            } else {
                //第二次 我们将没有获得锁的节点的前置节点设置为tail，其实也是head
           
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    //接着，尝试把下一个节点设置为tail节点
                    //如果成功，将会保证这是一个head 和 tail 都是 传进来
                    // node的节点
                    t.next = node;
                    return t;
                }
            }
        }
    }
```

首先，这是第一次循环的同步队列的样子

![1561989334765](https://github.com/PopCandier/Thread-Demo/blob/master/images/1561989334765.png)

很显然，第一次循环刚好初始化好了这个列表，即便这里面是个空的Node

接着第二次的循环，将包装进来的新的Node的prev设置成头结点(head)，不过由于

上一个循环有一个`tail=head`所以意味着这个时候head和tail节点其实引用的是同一个node

接着第二次循环

![1561989909176](https://github.com/PopCandier/Thread-Demo/blob/master/images/1561989909176.png)

这样，就相当于初始化完成，并且返回这个节点

```java
public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```

返回的这个节点，将作为参数传递给acquireQueued方法，去竞争锁

```java
 final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {//返回这个node的前一个节点
                final Node p = node.predecessor();
                //如果前一个节点是head节点，意味着，这个node将会有资格
                //去争抢锁资格
                //再次争抢锁，或者获得重入锁
                if (p == head && tryAcquire(arg)) {
                    //争抢成功后，意味着head节点已经释放了锁，
                    //将自己设置为head，并且将头节点的下一个节点设置为null
                    //这样，prev和next都会null，cg回收
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                //如果你的前一个节点不是头结点，那么你就没有资格争抢锁
                //你只能被挂起
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
//这个方法，返回head
 final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }
```

我们来讲讲这段代码

```java
if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
```

我们需要知道的是，当你的前一个节点不是头节点的是否，那就说明你是更后面的节点，你是需要被挂起的。

`shouldParkAfterFailedAcquire(p, node)`方法有两个参数，一个是当前节点的前一个节点，另一个是当前节点。

```java
 private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {		//我们知道，这个Node的状态应该是 Node.EXCLUSIVE
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)//所以，这里不走
        //到了这一步就是要判断是否挂起的状态。
            return true;
        if (ws > 0) {
            
            do {
                //将会把pred这个节点，取消掉
                //这里地方，是从当前节点往前迭代，找到cancel标志的节点
                //然后取消掉
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            //最后重新建立连接。
            pred.next = node;//重新建立关系
        } else {
            //设置成SIGNAL状态
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

//插播一段题外话
 Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
}
//其实还是可以能看出明显差别
Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
}
```

这一步就是根据waitStatus来决定是否要挂起线程，最后，会有一个cancelAcquire取消获得锁的操作。

关于状态将会几种状态

* CANCELLED(1)：在同步队列中等待的线程等待超时或被中断(intercept)，需要从同步队列取消该结点，其结点的waitStatus为CANCELLED，即结束状态，进入改状态的结点将不会发生变化。
* SIGNAL(-1)：只要前置节点释放锁，就会通知标识为SIGNAL状态的后续节点
* CONDITION（-2）：和Condition有关系，暂时不讲
* PROPAGATE（-3）：共享模式下，PROPAGATE 状态下的线程处于可运行状态
* 0 是初始状态

接下来就是挂起了，能来到这一步的都是返回了true的情况。

```java
private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);//挂起
    //注意，这个地方，线程就会阻塞在这里，如果唤醒才会接着执行
        return Thread.interrupted();//如果外面尝试调用了interrupt方法，尝试挂起该线程，该线程将会返回中断中断状态，并且复位。
    }

//最终，将会反应到最上面的acquire方法
public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();//将会中断自己。
    }
```

##### 锁的释放

```java
public void unlock() {
        sync.release(1);//很明显，这里是释放的锁的次数
    }

public final boolean release(int arg) {
        if (tryRelease(arg)) {//释放锁状态成功，必须全部释放完了
            Node h = head;//拿到aqs中的head节点
            /*
            h 不为空，并且头结点不是canncel状态
            那么我们将唤醒下一个节点去获得锁
            */
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }

protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;//释放锁完成后，设置独占为空
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;//知道锁全部释放完毕，也就是state为0的时候，才会true
        }
```

后续唤醒的操作

```java
private void unparkSuccessor(Node node) {
     
        int ws = node.waitStatus;
        if (ws < 0)//说明不是canncel状态
            compareAndSetWaitStatus(node, ws, 0);//尝试将头结点设置为无状态
        Node s = node.next;//拿到下一个节点
    	//下一个节点是空，或者 是Canncel状态的话。
        if (s == null || s.waitStatus > 0) {
            s = null;//设置成空
            
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)//也是一个剔除Cancel节点的过程
                    //从tail 往 前遍历，遍历到当前传入的节点。找到不为CANCEL的节点
                    s = t;
        }
        if (s != null)//然后唤醒
            LockSupport.unpark(s.thread);
    }

/*

！！！为什么释放锁的时候，是从tail往前进行扫描的。

首先回到添加node的方法，也就是enq方法中

*/
private Node enq(final Node node) {
     for (;;) {
     Node t = tail;
     if (t == null) { // Must initialize
     if (compareAndSetHead(new Node()))
     tail = head;
     } else {
         node.prev = t;//请注意这里
         if (compareAndSetTail(t, node)) {//首先这里是原子方法
         //可能存在执行到这一步的时候，某个线程unlock了
         /*
         unlock后，我们知道将会释放锁，并且调用unparkSuccessor方法
         这个时候，由于t.next=node这个方法还未调用，所以这个节点是一个
         只有prev而没有next的节点，所以从调用prev，可以保证原子性
         
         */
         t.next = node;
         return t;
         }
     }
     }
}


//接着，我们就会回到这里的代码，开始另一个线程的工作
final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())//回到这里
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
 private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
     //从这里被唤醒，如果他之前有被人尝试中断过，那么这个线程将会直接中断。
        return Thread.interrupted();
    }
```

#### 总结

简单说一下AQS所谓的同步Node队列的兴路历程

这里只说主线内容。

在非公平锁中，首先lock方法会调用aquire方法，去获得锁

在这里面有三个方法 tryaquire和aquireQueuen和addWaiter方法

第一次tryaquire是线程第一次不包装成node，去尝试获得锁的方法，这方法只允许你获得锁，又或者允许你重入一次锁。

当你失败后，你会在addWaiter方法中，被描述成Node.EXCLUSIVE的独占Node节点。

然后你会有第二次竞争锁的机会，就是在aquireQueuen中，在那里面，你将会被判断你是否是获得锁的下一个节点，如果有，你将会有资格获得锁(前提是，锁已经被释放。)

如果都失败，那么你将会被挂起。
