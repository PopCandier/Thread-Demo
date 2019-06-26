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





