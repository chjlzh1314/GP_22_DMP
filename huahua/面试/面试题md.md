#### 面试中的问题（重点）

1. ##### RDD的特性（RDD的解释）

2. ##### RDD的两类算子

3. ##### 算子原理（shuffle算子原理，引出Shuffle原理）

4. ##### Shuffle原理（和Hadoop的shuffle区别）

5. ##### 检查点、持计划、共享变量

6. ##### 分区（自定义分区、默认分区，区别，作用）

7. ##### 并行度（跑任务的并行度）参考调优文档1.2.3

8. ##### Spark的任务运行原理（重点中的重点）

9. ##### Task原理（本地化级别）

10. ##### DAG的原理（源码级别）

11. ##### SparkSQL和Hive区别

12. ##### DF和DS的之间的关系（从它的类型出发）

13. ##### 窗口函数（排名函数）

14. ##### SparkSQL-UDF（自定义函数）

15. ##### SparkStreaming对接Kafka的两种方式（重点）

16. ##### 如何解决数据积压问题（反压机制或者增加分区和消费者）

17. ##### 如何保证数据一致性问题（生产者和消费者）

18. ##### Kafka数据传输机制（参考图）

19. ##### Kafka如何保证数据不丢失（消费者）

20. ##### redis数据类型

21. ##### redis的持久化

22. ##### redis的击穿和雪崩（参考面试宝典，最下面）

23. ##### redis的集群原理（集群实现需要注意什么）

24. ##### SparkStreaming累加操作（参考UpdataStateBykey和MapwithState）

25. ##### ForeachRDD和Transform的区别

26. ##### 算子的区别（MapPartitions和Map或者foreach和foreachPartition区别）

27. ##### 什么是DStream

28. ##### 调优文档（参考Spark调优）

29. ##### 数据倾斜解决方案（参考Spark内核解析和调优指南）

30. ##### JVM调优（参考Spark调优文档）

31. ##### GC垃圾回收机制（算法原理）

    ![](C:\Users\97104\Desktop\Java内存.png)

    ~~~text
    一）GC的主要任务
    	1.分配内存
    	2.确保被引用对象的内存不被错误的回收
    	3.回收不再被引用的对象的内存空间
    二）流程
    	新建的对象在新生代中，如果新生代内存不足，Minor GC释放不活跃对象
    	如果还不够，将部分活跃对象复制到老年代中，还是不够minor GC释放老年代对象
    	如果还不够，JVM会抛出内存不足oom问题
    三）算法
    	1.引用计数算法
    		分析：是GC的早期策略。堆中的每一个对象案例都有一个引用计数
    		当一个对象被创建时，就将该对象实例分配给一个变量，该变量计数设为1
    		当任何其他变量被赋值为这个对象的引用的时候，引用计数加1
    		当一个对象实例的某个引用超过了生命周期或者被设置为一个新的值时，技术减1
    		任何计数器为0的对象案例被认为是垃圾收集
    	  优点：引用计数器可以很快地执行，交织在程序运行时。对程序需要长时间不被打断的实时环境比较有利
    	  缺点：无法检测出循环引用
    	2.可达性分析算法
    		分析：从一个节点，GC Root开始，寻找对应的引用节点，找到这个节点以后，继续寻找这个节点的引用节点，当有用的节点寻找完毕后，剩余的节点被认为是没有被引用的对象，即无用的节点，被判定为可回收的对象
    		Java中适合GC Root的对象：
    			a）虚拟机栈中引用的对象
    			b）方法中类静态属性引用的对象
    			c）方法区中常量的引用对象
    			d）本地方法中JNI（Native方法）引用的对象
    ~~~

32. ##### 手写快排或者其他算法（基础算法）

33. ##### Spark的内存模型

34. ##### 手写单例模式

35. ##### spark-on-yarn

    ~~~text
    spark-on-yarn
    
    两种模式的区别：
    	cluster模式：
    		Driver程序在YARN中运行，应用的运行结果不能在客户端显示，所以最好运行那些将结果最终保存在外部存储介质（如HDFS、Redis、Mysql）而非stdout输出的应用程序，客户端的终端显示的仅是作为YARN的job的简单运行状况。
    	client模式：
    		Driver运行在Client上，应用程序运行结果会在客户端显示，所有适合运行结果有输出的应用程序（如spark-shell）
    
    原理
    
    cluster模式：
    Spark Driver首先作为一个ApplicationMaster在YARN集群中启动，客户端提交给ResourceManager的每一个job都会在集群的NodeManager节点上分配一个唯一的ApplicationMaster，由该ApplicationMaster管理全生命周期的应用。具体过程：
    1、由client向ResourceManager提交请求，并上传jar到HDFS上
    	这期间包括四个步骤：
        a).连接到RM
        b).从RM的ASM（ApplicationsManager）中获得metric、queue和resource等信息。
        c). upload app jar and spark-assembly jar
        d).设置运行环境和container上下文（launch-container.sh等脚本)
    2、ResouceManager向NodeManager申请资源，创建SparkApplicationMaster（每个SparkContext都有一个ApplicationMaster）
    3、NodeManager启动ApplicationMaster，并向ResourceManagerAsM注册
    4、ApplicationMaster从HDFS中找到jar文件，启动SparkContext、DAGscheduler和YARN ClusterScheduler
    5、ResourceManager向ResourceManagerAsM注册申请container资源
    6、ResourceManager通知NodeManager分配Container，这时可以收到来自ASM关于container的报告。（每个container对应一个executor）
    7、Spark ApplicationMaster直接和container（executor）进行交互，完成这个分布式任务。
    
    client模式：
    	在client模式下，Driver运行在Client上，通过ApplicationMaster向RM获取资源。本地Driver负责与所有的executor container进行交互，并将最后的结果汇总。结束掉终端，相当于kill掉这个spark应用。一般来说，如果运行的结果仅仅返回到terminal上时需要配置这个。
    	客户端的Driver将应用提交给Yarn后，Yarn会先后启动ApplicationMaster和executor，另外ApplicationMaster和executor都是装载在container里运行，container默认的内存是1G，ApplicationMaster分配的内存是driver- memory，executor分配的内存是executor-memory。同时，因为Driver在客户端，所以程序的运行结果可以在客户端显示，Driver以进程名为SparkSubmit的形式存在。
    ~~~

#### hadoop

1. ##### HDFS文件存储机制（读写流程）

2. ##### MR的原理（Map和Reduce）

3. ##### Shuffle原理

4. ##### Hive的内部和外部表

5. ##### Hive的动态分区

   ~~~text
   动态分区：对分区表的数据会自动根据分区字段的值，将数据插入到相应的分区中
   参数：
   	set hive.exec.dynamic.partition=true
   	set hive.exec.dynamic.partition.mode=nonstrict
   区别：
   	静态分区：
   		1.需要手动指定分区名字
   		2.有可能存在某一分区没有一条数据的情况
   		3.静态分区列表是在编译时期通过用户传递决定的
   	动态分区：
   		1.根据查询语句自动生成分区名
   		2.每一个分区至少存在一条数据
   		3.在SQL执行的时候才确定将数据插入到哪一个分区中
   		4.动态分区的消耗性能远大于静态分区。在严格模式下，至少存在一个静态分区
   ~~~

6. ##### hive分区分桶

7. ##### Hive和Mysql区别

8. ##### Hive和Hbase区别

9. ##### Hive的调优（参考面试宝典）

10. ##### HBASE的热点问题（什么时候会触发，如何避免）

11. ##### Hbase的原理（读取和存储）

12. ##### HbaseRowKey设计原理

13. ##### Hbase和其他数据库相比的优势（特点）

14. ##### Flume的Source源有哪些

15. ##### Flume高可用（怎么实现高可用）

16. ##### Linux命令、HDFS命令（基础命令）

17. ##### Zookeeper的选举机制（内部如何实现）

18. ##### Oozie和Azkaban区别（主要是配置）

19. ##### 布隆过滤器（原理）

    ~~~text
    
    ~~~

    

### 掌握一到两种算法（原理）